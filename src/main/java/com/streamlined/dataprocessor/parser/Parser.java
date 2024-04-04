package com.streamlined.dataprocessor.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.dataprocessor.entity.Entity;

public class Parser<T extends Entity<?>> {

	private static final String FILE_PATTERN = "*.json";
	private static final int BUFFER_SIZE = 16 * 1024;

	private final ObjectMapper mapper;
	private final CollectionType collectionType;

	public Parser(Class<T> entityClass) {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		collectionType = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
	}

	public Stream<T> loadData(Path dataPath) {
		try (var pathStream = Files.newDirectoryStream(dataPath, FILE_PATTERN)) {
			Stream<T> stream = Stream.empty();
			for (var sourceFilePath : pathStream) {
				stream = Stream.concat(stream, loadDataFromFile(sourceFilePath));
			}
			return stream;
		} catch (IOException e) {
			throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
		}
	}

	private Stream<T> loadDataFromFile(Path filePath) {
		try (var inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);
				var bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE)) {
			return parseFile(bufferedInputStream);
		} catch (IOException e) {
			throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
		}
	}

	private Stream<T> parseFile(InputStream inputStream) throws IOException {
		List<T> list = mapper.readValue(inputStream, collectionType);
		return list.stream();
	}

}
