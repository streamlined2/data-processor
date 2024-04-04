package com.streamlined.dataprocessor.processor;

import java.util.Comparator;
import java.util.Objects;

public record Item(Object value, Long count) {

	public static final Comparator<Item> COMPARATOR_BY_COUNT_DESC = Comparator.comparing(Item::count).reversed()
			.thenComparing(Item::compareByValue);

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
