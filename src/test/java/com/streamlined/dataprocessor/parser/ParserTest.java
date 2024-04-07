package com.streamlined.dataprocessor.parser;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.streamlined.dataprocessor.entity.Person;

class ParserTest {

	private static final int NUMBER_OF_THREADS = 8;

	private Parser<Person> parser;

	@BeforeEach
	void setUp() throws Exception {
		parser = new Parser<>(Person.class, NUMBER_OF_THREADS);
	}

	@ParameterizedTest
	@ValueSource(strings = { """
				[
				{
					"name": "John Smith",
					"birthday": "1970-01-01",
					"sex": "MALE",
					"eyeColor": "GREEN",
					"hairColor": "BLACK",
					"weight": 60.20,
					"height": 185.00,
					"countryOfOrigin": {
						"name": "Great Britain",
						"continent": "EUROPE",
						"capital": "London",
						"population": 60000000,
						"square": 1745813.01
					},
					"citizenship": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"favoriteMeals":"potatoes,meat,fish"
				},
				{
					"name": "Jacky Bauer",
					"birthday": "1980-06-20",
					"sex": "FEMALE",
					"eyeColor": "BLUE",
					"hairColor": "YELLOW",
					"weight": 55.50,
					"height": 165.00,
					"countryOfOrigin": {
						"name": "Germany",
						"continent": "EUROPE",
						"capital": "Berlin",
						"population": 75000000,
						"square": 1279813.01
					},
					"citizenship": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"favoriteMeals":"meat,fish,pasta"
				},
				{
					"name": "Richard Stonewall",
					"birthday": "1965-07-15",
					"sex": "MALE",
					"eyeColor": "BROWN",
					"hairColor": "GRAY",
					"weight": 120.20,
					"height": 170.00,
					"countryOfOrigin": {
						"name": "New Zealand",
						"continent": "AUSTRALIA",
						"capital": "Wellington",
						"population": 15000000,
						"square": 998817.01
					},
					"citizenship": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"favoriteMeals":"fish,pasta,apple"
				},
				{
					"name": "Sandy Fisher",
					"birthday": "1986-12-01",
					"sex": "FEMALE",
					"eyeColor": "BLUE",
					"hairColor": "GRAY",
					"weight": 75.00,
					"height": 175.00,
					"countryOfOrigin": {
						"name": "Canada",
						"continent": "NORTH_AMERICA",
						"capital": "Ottawa",
						"population": 125000000,
						"square": 6345813.01
					},
					"citizenship": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"favoriteMeals":"pasta,apple,pear"
				},
				{
					"name": "Ken Winston",
					"birthday": "2000-04-10",
					"sex": "MALE",
					"eyeColor": "RED",
					"hairColor": "GREEN",
					"weight": 80.50,
					"height": 205.00,
					"countryOfOrigin": {
						"name": "Netherlands",
						"continent": "EUROPE",
						"capital": "Amsterdam",
						"population": 35000000,
						"square": 425853.01
					},
					"citizenship": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"favoriteMeals":"apple,pear,grape"
				},
				{
					"name": "Sunny Smile",
					"birthday": "2010-03-05",
					"sex": "FEMALE",
					"eyeColor": "BROWN",
					"hairColor": "YELLOW",
					"weight": 50.20,
					"height": 130.00,
					"countryOfOrigin": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"citizenship": {
						"name": "USA",
						"continent": "NORTH_AMERICA",
						"capital": "Washington",
						"population": 250000000,
						"square": 6361952.20
					},
					"favoriteMeals":"pear,grape,meat"
				}
			]

						""" })
	void testParseFile(String entityList) {
		var parsedData = parser.loadData(new String[] { entityList }).toList();

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
