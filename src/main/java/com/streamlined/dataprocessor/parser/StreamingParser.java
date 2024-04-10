package com.streamlined.dataprocessor.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonToken;

public class StreamingParser {

	private static final Logger log = Logger.getLogger(StreamingParser.class.getName());

	private static final String SOURCE_FILE_PATTERN = "*.json";
	private static final int SOURCE_FILE_QUEUE_INITIAL_CAPACITY = 100;
	private static final int RESULT_QUEUE_INITIAL_CAPACITY = 10_000;

	private final int numberOfThreads;
	private final ExecutorService executorService;
	private final BlockingQueue<Path> sourceFileQueue;
	private final BlockingQueue<String> resultQueue;
	private final JsonFactory jsonFactory;
	private final AtomicInteger finishedThreadCount;

	public StreamingParser(int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
		finishedThreadCount = new AtomicInteger(0);
		executorService = Executors.newFixedThreadPool(numberOfThreads);
		sourceFileQueue = new ArrayBlockingQueue<>(SOURCE_FILE_QUEUE_INITIAL_CAPACITY);
		resultQueue = new ArrayBlockingQueue<>(RESULT_QUEUE_INITIAL_CAPACITY);
		jsonFactory = new JsonFactory();
	}

	public Stream<String> stream(Path path, String propertyName) {
		StreamingIterable iterable = new StreamingIterable(path, propertyName);
		return StreamSupport.stream(iterable.spliterator(), false).filter(Objects::nonNull);
	}

	public boolean isDone() {
		return finishedThreadCount.intValue() == numberOfThreads && resultQueue.isEmpty();
	}

	public void startParsing(Path dataPath, String propertyName) {
		sourceFileQueue.clear();
		resultQueue.clear();
		try (var pathStream = Files.newDirectoryStream(dataPath, SOURCE_FILE_PATTERN)) {
			pathStream.forEach(sourceFileQueue::add);
			startParseTasks(propertyName);
			executorService.shutdown();
		} catch (IOException e) {
			log.severe(() -> "Error iterating through directory %s".formatted(dataPath));
			throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
		}
	}

	private void startParseTasks(String propertyName) {
		for (int k = 0; k < numberOfThreads; k++) {
			executorService.submit(new ParseTask(propertyName));
		}
	}

	private class ParseTask implements Runnable {

		private final String propertyName;

		private ParseTask(String propertyName) {
			this.propertyName = propertyName;
		}

		@Override
		public void run() {
			for (Path filePath = null; (filePath = sourceFileQueue.poll()) != null;) {
				parseFile(filePath, propertyName);
			}
			finishedThreadCount.incrementAndGet();
		}

		private void parseFile(Path filePath, String propertyName) {
			try (var reader = Files.newBufferedReader(filePath); var jsonParser = jsonFactory.createParser(reader)) {
				if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
					throw new ParseException("Document should represent array of entities and start with [");
				}
				while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
					if (propertyName.equals(jsonParser.currentName())) {
						jsonParser.nextToken();
						addValue(jsonParser.getText());
						while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
						}
					}
				}
			} catch (IOException e) {
				log.severe(() -> "Error parsing file %s".formatted(filePath.toAbsolutePath()));
				throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
			}
		}

		private void addValue(String value) {
			try {
				resultQueue.put(value);
			} catch (InterruptedException e) {
				log.severe("Interrupted while waiting for free space in result queue");
				throw new ParseException("Interrupted while waiting for free space in result queue");
			}
		}
	}

	private class StreamingIterable implements Iterable<String> {

		private StreamingIterable(Path path, String propertyName) {
			startParsing(path, propertyName);
		}

		@Override
		public Iterator<String> iterator() {
			return new StreamingIterator();
		}

		private class StreamingIterator implements Iterator<String> {
			@Override
			public boolean hasNext() {
				return !isDone();
			}

			@Override
			public String next() {
				if (isDone()) {
					throw new NoSuchElementException("No more elements left");
				}
				String value = null;
				while (!isDone() && (value = resultQueue.poll()) == null) {
				}
				return value;
			}
		}
	}
}
