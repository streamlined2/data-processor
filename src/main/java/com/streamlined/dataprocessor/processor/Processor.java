package com.streamlined.dataprocessor.processor;

import java.lang.reflect.Method;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamlined.dataprocessor.entity.Entity;

public class Processor<T extends Entity<?>> {

	private final Class<T> entityClass;

	public Processor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	public ProcessingResult process(Stream<T> entityStream, String propertyName) {
		var map = entityStream
				.collect(Collectors.groupingBy(entity -> getKeyValue(entity, propertyName), Collectors.counting()));
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

	private String getGetterName(String propertyName) {
		return "get" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
	}

}
