package com.streamlined.dataprocessor.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.dataprocessor.entity.Entity;
import com.streamlined.dataprocessor.entity.Person;

public class Parser<T extends Entity<?>> {

	private static final String FILE_PATTERN = "*.json";
	private static final int BUFFER_SIZE = 16 * 1024;

	private final ObjectMapper mapper;
	private final Class<T> entityClass;

	public Parser(Class<T> entityClass) {
		this.entityClass = entityClass;
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	public List<T> loadData(Path dataPath) {
		try (var pathStream = Files.newDirectoryStream(dataPath, FILE_PATTERN)) {
			List<T> list = new LinkedList<>();
			for (var sourceFilePath : pathStream) {
				list.addAll(loadDataFromFile(sourceFilePath));
			}
			return list;
		} catch (IOException e) {
			throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
		}
	}

	private List<T> loadDataFromFile(Path filePath) {
		try (var inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);
				var bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE)) {
			CollectionType collectionType = mapper.getTypeFactory().constructCollectionType(List.class, entityClass);
			return mapper.readValue(bufferedInputStream, collectionType);
		} catch (IOException e) {
			throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
		}
	}

	public static void main(String... args) {
		var parsedData = new Parser<Person>(Person.class).loadData(Path.of("src", "main", "resources"));
		parsedData.forEach(System.out::println);
	}

}
