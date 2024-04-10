package com.streamlined.dataprocessor.parser;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.streamlined.dataprocessor.entity.Person;

class MultithreadParsePerformanceTest {

	private static final Path TEST_DATA_DIRECTORY = Path.of("src/main/resources/data");
	private static final String PROPERTY_NAME = "favoriteMeals";
	private static final int MEASURE_COUNT = 3;

	@ParameterizedTest
	@ValueSource(ints = { 1, 1, 2, 4, 8 })
	void measureParseTime(int threadCount) {
		long duration = 0;
		for (int k = 0; k < MEASURE_COUNT; k++) {
			var parser = new Parser<>(Person.class, threadCount);
			LocalDateTime startTime = LocalDateTime.now();
			parser.loadData(TEST_DATA_DIRECTORY).toList();
			LocalDateTime finishTime = LocalDateTime.now();
			duration += Duration.between(startTime, finishTime).toMillis();
			System.gc();
		}
		System.out.printf("Number of threads %d, parsing duration %d msec%n", threadCount, duration / MEASURE_COUNT);
	}

	@ParameterizedTest
	@ValueSource(ints = { 1, 1, 2, 4, 8 })
	void measureStreamingParseTime(int threadCount) {
		long duration = 0;
		for (int k = 0; k < MEASURE_COUNT; k++) {
			var parser = new StreamingParser(threadCount);
			LocalDateTime startTime = LocalDateTime.now();
			parser.stream(TEST_DATA_DIRECTORY, PROPERTY_NAME).count();
			LocalDateTime finishTime = LocalDateTime.now();
			duration += Duration.between(startTime, finishTime).toMillis();
			//System.gc();
		}
		System.out.printf("Number of threads %d, parsing duration %d msec via Streaming API%n", threadCount, duration / MEASURE_COUNT);
	}

}
