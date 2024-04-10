package com.streamlined.dataprocessor.processor;

import java.io.IOException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public final class ProcessingResult implements Iterable<Item> {

	private static final Comparator<Item> COMPARATOR_BY_COUNT_DESC = Comparator.comparing(Item::count).reversed()
			.thenComparing(Item::compareByValue);

	private final SortedSet<Item> items;

	public ProcessingResult(Map<Object, Long> map) {
		items = new TreeSet<>(COMPARATOR_BY_COUNT_DESC);
		for (var entry : map.entrySet()) {
			items.add(new Item(entry.getKey(), entry.getValue()));
		}
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	public int size() {
		return items.size();
	}

	@Override
	public Iterator<Item> iterator() {
		return items.iterator();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder("[");
		var i = iterator();
		if (i.hasNext()) {
			b.append(i.next());
			while (i.hasNext()) {
				b.append(",").append(i.next());
			}
		}
		b.append("]");
		return b.toString();
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
			for (var i : value.items) {
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
