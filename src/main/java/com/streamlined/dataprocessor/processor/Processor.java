package com.streamlined.dataprocessor.processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamlined.dataprocessor.entity.Entity;

public class Processor<T extends Entity<?>> {

	private static final String KEY_SEPARATOR = ",";

	private final Class<T> entityClass;

	public Processor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public ProcessingResult process(Stream<T> entityStream, String propertyName) {
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
			throw new ProcessingException(
					"No accessible getter method %s found for entity class %s or it's execution failed"
							.formatted(getterName, entityClass.getSimpleName()),
					e);
		}
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

}
