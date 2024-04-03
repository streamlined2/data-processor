package com.streamlined.dataprocessor.processor;

public record Item(Object value, Long count) {

	@Override
	public String toString() {
		return "%s: %s".formatted(value, count);
	}
}
