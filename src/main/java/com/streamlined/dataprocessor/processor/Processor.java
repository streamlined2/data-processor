package com.streamlined.dataprocessor.processor;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamlined.dataprocessor.entity.Entity;
import com.streamlined.dataprocessor.entity.Person;
import com.streamlined.dataprocessor.parser.Parser;

public class Processor<T extends Entity<?>> {

	private final Class<T> entityClass;

	public Processor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public Stream<ProcessingResult> process(Stream<T> entityStream, String propertyName) {
		return entityStream
				.collect(Collectors.groupingBy(entity -> getKeyValue(entity, propertyName), Collectors.counting()))
				.entrySet().stream().map(ProcessingResult::new);
	}

	private Object getKeyValue(T entity, String propertyName) {
		final String getterName = getGetterName(propertyName);
		try {
			Method getter = entityClass.getDeclaredMethod(getterName);
			return getter.invoke(entity);
		} catch (ReflectiveOperationException | SecurityException e) {
			throw new ProcessingException("No accessible getter method %s found for entity class %s or it's execution failed"
					.formatted(getterName, entityClass.getSimpleName()), e);
		}
	}

	private String getGetterName(String propertyName) {
		return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}

	public static void main(String... args) {
		var parsedData = new Parser<Person>(Person.class).loadData(Path.of("src", "main", "resources"));
		Processor<Person> processor = new Processor<>(Person.class);
		var processedData = processor.process(parsedData, "hairColor");
		processedData.forEach(System.out::println);
	}

}
