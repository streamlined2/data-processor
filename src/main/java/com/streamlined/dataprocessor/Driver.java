package com.streamlined.dataprocessor;

import java.io.File;
import java.nio.file.Path;

import com.streamlined.dataprocessor.entity.Entity;
import com.streamlined.dataprocessor.entity.Person;
import com.streamlined.dataprocessor.parser.Parser;
import com.streamlined.dataprocessor.processor.Processor;
import com.streamlined.dataprocessor.reporter.Reporter;

public class Driver<T extends Entity<?>> {

	private static final Path RESULT_FILE_DIRECTORY = Path.of("src/main/resources/data");
	private static final String FILE_NAME_PREFIX = "statistics_by_";
	private static final String FILE_TYPE = ".xml";
	private static final int NUMBER_OF_THREADS = 8;

	private final Parser<T> parser;
	private final Processor<T> processor;
	private final Reporter reporter;

	public Driver(Class<T> entityClass, int numberOfThreads) {
		parser = new Parser<>(entityClass, numberOfThreads);
		processor = new Processor<>(entityClass);
		reporter = new Reporter();
	}

	public void doWork(Path sourceDirectory, String propertyName) {
		var parsedData = parser.loadData(sourceDirectory);
		var processedData = processor.process(parsedData, propertyName);
		reporter.save(getResultFile(propertyName), processedData);
	}

	private Path getResultFile(String propertyName) {
		return new File(RESULT_FILE_DIRECTORY.toFile(), FILE_NAME_PREFIX + propertyName + FILE_TYPE).toPath();
	}

	public static void main(String... args) {
		new Driver<Person>(Person.class, NUMBER_OF_THREADS).doWork(RESULT_FILE_DIRECTORY, "favoriteMeals");
	}

}
