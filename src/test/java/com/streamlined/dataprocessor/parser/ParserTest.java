package com.streamlined.dataprocessor.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Path;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.streamlined.dataprocessor.entity.Person;

class ParserTest {

	private static final Path TEST_DATA_DIRECTORY = Path.of("src/main/resources");
	private static final int NUMBER_OF_THREADS = 8;

	private Parser<Person> parser;

	@BeforeEach
	void setUp() throws Exception {
		parser = new Parser<>(Person.class, NUMBER_OF_THREADS);
	}

	@Test
	void testParseFile() {
		var parsedData = parser.loadData(TEST_DATA_DIRECTORY).toList();

		assertEquals(6, parsedData.size());

		assertEquals("John Smith", parsedData.get(0).getName());
		assertEquals("Jacky Bauer", parsedData.get(1).getName());
		assertEquals("Richard Stonewall", parsedData.get(2).getName());
		assertEquals("Sandy Fisher", parsedData.get(3).getName());
		assertEquals("Ken Winston", parsedData.get(4).getName());
		assertEquals("Sunny Smile", parsedData.get(5).getName());

		assertEquals(LocalDate.of(1970, 1, 1), parsedData.get(0).getBirthday());
		assertEquals(LocalDate.of(1980, 6, 20), parsedData.get(1).getBirthday());
		assertEquals(LocalDate.of(1965, 7, 15), parsedData.get(2).getBirthday());
		assertEquals(LocalDate.of(1986, 12, 1), parsedData.get(3).getBirthday());
		assertEquals(LocalDate.of(2000, 4, 10), parsedData.get(4).getBirthday());
		assertEquals(LocalDate.of(2010, 3, 5), parsedData.get(5).getBirthday());

		assertEquals("potatoes,meat,fish", parsedData.get(0).getFavoriteMeals());
		assertEquals("meat,fish,pasta", parsedData.get(1).getFavoriteMeals());
		assertEquals("fish,pasta,apple", parsedData.get(2).getFavoriteMeals());
		assertEquals("pasta,apple,pear", parsedData.get(3).getFavoriteMeals());
		assertEquals("apple,pear,grape", parsedData.get(4).getFavoriteMeals());
		assertEquals("pear,grape,meat", parsedData.get(5).getFavoriteMeals());

	}

}
