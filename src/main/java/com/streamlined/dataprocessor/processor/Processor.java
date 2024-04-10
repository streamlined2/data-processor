package com.streamlined.dataprocessor.processor;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamlined.dataprocessor.entity.Entity;
import com.streamlined.dataprocessor.entity.Person;
import com.streamlined.dataprocessor.parser.StreamingParser;

public class Processor<T extends Entity<?>> {

	private static final Logger log = Logger.getLogger(Processor.class.getName());

	private static final String KEY_SEPARATOR = ",";

	private final Class<T> entityClass;

	public Processor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public ProcessingResult processEntityStream(Stream<T> entityStream, String propertyName) {
		var map = entityStream.map(entity -> getKeyValue(entity, propertyName)).flatMap(this::splitKey)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		return new ProcessingResult(map);
	}

	private Object getKeyValue(T entity, String propertyName) {
		final String getterName = getGetterName(propertyName);
		try {
			Method getter = entityClass.getDeclaredMethod(getterName);
			return getter.invoke(entity);
		} catch (ReflectiveOperationException | SecurityException e) {
			log.severe(() -> "No accessible getter method %s found for entity class %s or it's execution failed"
					.formatted(getterName, entityClass.getSimpleName()));
			throw new ProcessingException(
					"No accessible getter method %s found for entity class %s or it's execution failed"
							.formatted(getterName, entityClass.getSimpleName()),
					e);
		}
	}

	public ProcessingResult processKeyStream(Stream<String> keyStream) {
		var map = keyStream.flatMap(this::splitKey)
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		return new ProcessingResult(map);
	}

	private Stream<Object> splitKey(Object key) {
		if (key instanceof String string) {
			return Arrays.stream(string.split(KEY_SEPARATOR));
		}
		return Stream.of(key);
	}

	private String getGetterName(String propertyName) {
		return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}
/*
	public static void main(String... args) {
		StreamingParser parser = new StreamingParser(8);
		var processor = new Processor<>(Person.class);
		//var result = processor.processKeyStream(parser.stream(Path.of("src/main/resources/data"), "eyeColor"));
		var result = processor.processKeyStream(parser.stream(Path.of("src/main/resources/data"), "favoriteMeals"));
		System.out.println(result);
	}
*/
}
