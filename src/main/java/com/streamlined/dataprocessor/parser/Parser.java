package com.streamlined.dataprocessor.parser;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.dataprocessor.entity.Entity;

public class Parser<T extends Entity<?>> {

	private static final String SOURCE_FILE_PATTERN = "*.json";
	private static final int SOURCE_FILE_QUEUE_INITIAL_CAPACITY = 100;
	private static final long PARSE_TASK_TERMINATION_TIMEOUT_SECONDS = 120;

	private final ObjectMapper mapper;
	private final CollectionType collectionType;
	private final int numberOfThreads;
	private final ExecutorService executorService;
	private final ArrayBlockingQueue<Path> sourceFileQueue;

	public Parser(Class<T> entityClass, int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
		executorService = Executors.newFixedThreadPool(numberOfThreads);
		sourceFileQueue = new ArrayBlockingQueue<>(SOURCE_FILE_QUEUE_INITIAL_CAPACITY);
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		collectionType = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
	}

	public Stream<T> loadData(Path dataPath) {
		try (var pathStream = Files.newDirectoryStream(dataPath, SOURCE_FILE_PATTERN)) {
			setUpFileQueue(pathStream);
			List<Future<List<T>>> futures = startParseTasks();
			executorService.shutdown();
			executorService.awaitTermination(PARSE_TASK_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			return collectTaskResults(futures);
		} catch (IOException e) {
			throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
		} catch (InterruptedException e) {
			throw new ParseException("Error while waiting for parsing threads", e);
		}
	}

	private void setUpFileQueue(DirectoryStream<Path> pathStream) {
		sourceFileQueue.clear();
		pathStream.forEach(sourceFileQueue::add);
	}

	private List<Future<List<T>>> startParseTasks() {
		List<Future<List<T>>> futures = new ArrayList<>();
		for (int k = 0; k < numberOfThreads; k++) {
			futures.add(executorService.submit(this::parseTask));
		}
		return futures;
	}

	private Stream<T> collectTaskResults(List<Future<List<T>>> futures) {
		try {
			Stream<T> stream = Stream.empty();
			for (var future : futures) {
				stream = Stream.concat(stream, future.get().stream());
			}
			return stream;
		} catch (InterruptedException | ExecutionException e) {
			throw new ParseException("Error while waiting for parsing threads", e);
		}
	}

	private List<T> parseTask() {
		List<T> entities = new ArrayList<>();
		for (Path filePath = null; (filePath = sourceFileQueue.poll()) != null;) {
			entities.addAll(parseFile(filePath));
		}
		return entities;
	}

	private List<T> parseFile(Path filePath) {
		try (var reader = Files.newBufferedReader(filePath)) {
			return mapper.readValue(reader, collectionType);
		} catch (IOException e) {
			throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
		}
	}

}
