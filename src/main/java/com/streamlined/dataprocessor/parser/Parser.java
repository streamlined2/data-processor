package com.streamlined.dataprocessor.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.dataprocessor.entity.Person;

public class Parser {

	private static final String FILE_PATTERN = "*.json";
	private static final int BUFFER_SIZE = 16 * 1024;

	private final ObjectMapper mapper;

	public Parser() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
	}

	public List<Person> loadData(Path dataPath) {
		try (var pathStream = Files.newDirectoryStream(dataPath, FILE_PATTERN)) {
			List<Person> personList = new LinkedList<>();
			for (var sourceFilePath : pathStream) {
				personList.addAll(loadDataFromFile(sourceFilePath));
			}
			return personList;
		} catch (IOException e) {
			throw new ParseException("Error iterating through directory %s".formatted(dataPath), e);
		}
	}

	private List<Person> loadDataFromFile(Path filePath) {
		try (var inputStream = Files.newInputStream(filePath, StandardOpenOption.READ);
				var bufferedInputStream = new BufferedInputStream(inputStream, BUFFER_SIZE)) {
			return mapper.readValue(bufferedInputStream, new TypeReference<List<Person>>() {
			});
		} catch (IOException e) {
			throw new ParseException("Error parsing file %s".formatted(filePath.toAbsolutePath()), e);
		}
	}

	public static void main(String... args) {
		var parsedData = new Parser().loadData(Path.of("src", "main", "resources"));
		parsedData.forEach(System.out::println);;
	}

}
