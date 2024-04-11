package com.streamlined.dataprocessor;

import java.io.File;
import java.nio.file.Path;

import com.streamlined.dataprocessor.entity.Entity;
import com.streamlined.dataprocessor.entity.Person;
import com.streamlined.dataprocessor.parser.Parser;
import com.streamlined.dataprocessor.processor.Processor;
import com.streamlined.dataprocessor.reporter.Reporter;

/**
 * Main class of the application which coordinates work of parser, processor,
 * reporter classes
 * 
 * @param <T> entity type
 */
public class Driver<T extends Entity<?>> {

	private static final Path DEFAULT_SOURCE_FILE_FOLDER = Path.of("src/main/resources");
	private static final String DEFAULT_PROPERTY_NAME = "favoriteMeals";
	private static final String RESULT_FILE_NAME_PREFIX = "statistics_by_";
	private static final String RESULT_FILE_TYPE = ".xml";
	private static final int NUMBER_OF_THREADS = 8;

	private final Parser<T> parser;
	private final Processor<T> processor;
	private final Reporter reporter;

	public Driver(Class<T> entityClass, int numberOfThreads) {
		parser = new Parser<>(entityClass, numberOfThreads);
		processor = new Processor<>(entityClass);
		reporter = new Reporter();
	}

	/**
	 * Method parses JSON documents from folder {@code sourceFileFolder}, extracts
	 * values of property {@code propertyName}, processes them, forms report and
	 * saves it as XML file
	 * 
	 * @param sourceFileFolder folder that contains source JSON documents
	 * @param propertyName     name of entity property
	 */
	public void doWork(Path sourceFileFolder, String propertyName) {
		var parsedData = parser.loadData(sourceFileFolder);
		var processedData = processor.processEntityStream(parsedData, propertyName);
		reporter.save(getResultFile(sourceFileFolder, propertyName), processedData);
	}

	private Path getResultFile(Path sourceFileFolder, String propertyName) {
		return new File(sourceFileFolder.toFile(), RESULT_FILE_NAME_PREFIX + propertyName + RESULT_FILE_TYPE).toPath();
	}

	/**
	 * Starting point of the application. Constructs parser, processor, reporter
	 * instances, and calls method {@code doWork}
	 * 
	 * @param args first parameter is folder that contains source JSON files to
	 *             parse, second parameter is name of the entity property which
	 *             should be processed
	 */
	public static void main(String... args) {
		String propertyName = DEFAULT_PROPERTY_NAME;
		if (args.length >= 2) {
			propertyName = args[1];
		}
		Path sourceFileFolder = DEFAULT_SOURCE_FILE_FOLDER;
		if (args.length >= 1) {
			sourceFileFolder = Path.of(args[0]);
		}
		new Driver<Person>(Person.class, NUMBER_OF_THREADS).doWork(sourceFileFolder, propertyName);
	}

}
