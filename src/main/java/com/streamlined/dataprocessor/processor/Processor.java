package com.streamlined.dataprocessor.processor;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.streamlined.dataprocessor.entity.Entity;

/**
 * Class to process stream of entities or property values
 */
public class Processor<T extends Entity<?>> {

	private static final Logger log = Logger.getLogger(Processor.class.getName());

	private static final String KEY_SEPARATOR = ",";

	private final Class<T> entityClass;

	public Processor(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	/**
	 * Method processes entity stream {@code entityStream}, extracts value of
	 * specified entity property {@code propertyName}, splits it if this is
	 * comma-separated string, and counts number of occurrences for each value
	 * 
	 * @param entityStream stream of entities
	 * @param propertyName name of entity property which value should be extracted
	 * @return instance of {@code ProcessingResult} that holds map of extracted
	 *         values and number of occurrences
	 */
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

	/**
	 * Method processes property value stream {@code keyStream}, splits property
	 * value if this is comma-separated string, and counts number of occurrences for each value
	 * 
	 * @param keyStream stream of property values
	 * @return instance of {@code ProcessingResult} that holds map of extracted
	 *         values and number of occurrences
	 */
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

}
