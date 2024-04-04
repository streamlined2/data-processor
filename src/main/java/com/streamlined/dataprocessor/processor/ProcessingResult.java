package com.streamlined.dataprocessor.processor;

import java.io.IOException;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class ProcessingResult {

	private final SortedSet<Item> item;

	public ProcessingResult(Map<Object, Long> map) {
		item = new TreeSet<>(Item.COMPARATOR_BY_COUNT_DESC);
		for (var entry : map.entrySet()) {
			item.add(new Item(entry.getKey(), entry.getValue()));
		}
	}

	public static class Serializer extends StdSerializer<ProcessingResult> {

		public Serializer() {
			this(null);
		}

		protected Serializer(Class<ProcessingResult> t) {
			super(t);
		}

		@Override
		public void serialize(ProcessingResult value, JsonGenerator gen, SerializerProvider provider)
				throws IOException {
			gen.writeStartObject();
			for (var i : value.item) {
				gen.writeFieldName("item");
				gen.writeStartObject();
				gen.writeObjectField("value", i.value());
				gen.writeObjectField("count", i.count());
				gen.writeEndObject();
			}
			gen.writeEndObject();
		}
	}
}
