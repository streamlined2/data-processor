package com.streamlined.dataprocessor.parser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
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

	public Parser(Class<T> entityClass, int numberOfThreads) {
		this.numberOfThreads = numberOfThreads;
		executorService = Executors.newFixedThreadPool(numberOfThreads);
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		collectionType = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
	}

	public Stream<T> loadData(Path dataPath) {
		try (var pathStream = Files.newDirectoryStream(dataPath, SOURCE_FILE_PATTERN)) {
			final BlockingQueue<Path> sourceFileQueue = new ArrayBlockingQueue<>(SOURCE_FILE_QUEUE_INITIAL_CAPACITY);
			pathStream.forEach(sourceFileQueue::add);
			final Callable<List<T>> taskCallable = () -> {
				List<T> entities = new ArrayList<>();
				for (Path filePath = null; (filePath = sourceFileQueue.poll()) != null;) {
					entities.addAll(parseFile(filePath));
				}
				return entities;
			};
			return executeTasks(taskCallable);
		} catch (IOException e) {
			throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
		}
	}

	private List<T> parseFile(Path filePath) {
		try (var reader = Files.newBufferedReader(filePath)) {
			return mapper.readValue(reader, collectionType);
		} catch (IOException e) {
			throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
		}
	}

	public Stream<T> loadData(String[] entityLists) {
		final BlockingQueue<String> sourceDocumentQueue = new ArrayBlockingQueue<>(entityLists.length);
		sourceDocumentQueue.addAll(Arrays.asList(entityLists));
		final Callable<List<T>> taskCallable = () -> {
			List<T> entities = new ArrayList<>();
			for (String document = null; (document = sourceDocumentQueue.poll()) != null;) {
				entities.addAll(parseDocument(document));
			}
			return entities;
		};
		return executeTasks(taskCallable);
	}

	private List<T> parseDocument(String document) {
		try {
			return mapper.readValue(document, collectionType);
		} catch (JsonProcessingException e) {
			throw new ParseException("Error parsing document %s".formatted(document), e);
		}
	}

	private Stream<T> executeTasks(Callable<List<T>> taskCallable) {
		try {
			List<Future<List<T>>> futures = startParseTasks(taskCallable);
			executorService.shutdown();
			executorService.awaitTermination(PARSE_TASK_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			return collectTaskResults(futures);
		} catch (InterruptedException e) {
			throw new ParseException("Error while waiting for parsing threads", e);
		}
	}

	private List<Future<List<T>>> startParseTasks(Callable<List<T>> taskCallable) {
		List<Future<List<T>>> futures = new ArrayList<>();
		for (int k = 0; k < numberOfThreads; k++) {
			futures.add(executorService.submit(taskCallable));
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

}
