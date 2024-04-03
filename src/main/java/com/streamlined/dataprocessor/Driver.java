package com.streamlined.dataprocessor;

import java.nio.file.Path;

import com.streamlined.dataprocessor.entity.Entity;
import com.streamlined.dataprocessor.entity.Person;
import com.streamlined.dataprocessor.parser.Parser;
import com.streamlined.dataprocessor.processor.Processor;
import com.streamlined.dataprocessor.reporter.Reporter;

public class Driver<T extends Entity<?>> {

	private static final Path RESULT_FILE = Path.of("src", "main", "resources", "result.xml");

	private final Parser<T> parser;
	private final Processor<T> processor;
	private final Reporter reporter;

	public Driver(Class<T> entityClass) {
		parser = new Parser<>(entityClass);
		processor = new Processor<>(entityClass);
		reporter = new Reporter();
	}

	public void doWork(Path sourceDirectory, String propertyName) {
		var parsedData = parser.loadData(sourceDirectory);
		var processedData = processor.process(parsedData, propertyName);
		reporter.save(RESULT_FILE, processedData);
	}

	public static void main(String... args) {
		new Driver<Person>(Person.class).doWork(Path.of("src/main/resources"), "hairColor");
	}

}
