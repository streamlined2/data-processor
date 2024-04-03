package com.streamlined.dataprocessor.processor;

import java.util.Map;

public record ProcessingResult(Object keyValue, Long count) {

	public ProcessingResult(Map.Entry<Object, Long> entry) {
		this(entry.getKey(), entry.getValue());
	}

	@Override
	public String toString() {
		return "%s: %s".formatted(keyValue, count);
	}
}
