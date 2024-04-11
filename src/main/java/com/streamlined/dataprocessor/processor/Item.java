package com.streamlined.dataprocessor.processor;

import java.util.Objects;

/**
 * Holds property value and number of its occurrences
 */
public record Item(Object value, Long count) {

	public int compareByValue(Item a) {
		return value.toString().compareTo(a.value().toString());
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Item item) {
			return Objects.equals(value, item.value);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public String toString() {
		return "%s: %s".formatted(value, count);
	}
}
