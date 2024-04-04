package com.streamlined.dataprocessor.processor;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.streamlined.dataprocessor.entity.Country;
import com.streamlined.dataprocessor.entity.Country.Continent;
import com.streamlined.dataprocessor.entity.Person;
import com.streamlined.dataprocessor.entity.Person.Color;
import com.streamlined.dataprocessor.entity.Person.Sex;

class ProcessorTest {

	private static final String VALID_PROPERTY_NAME = "favoriteMeals";
	private static final String INVALID_PROPERTY_NAME = "_non_valid_property_name";

	private Processor<Person> processor;

	@BeforeEach
	void setUp() throws Exception {
		processor = new Processor<>(Person.class);
	}

	@Test
	void emptyStreamValidPropertyName_process_processingResultShouldBeEmpty() {
		assertTrue(processor.process(Stream.empty(), VALID_PROPERTY_NAME).isEmpty());
	}

	@Test
	void emptyStreamInvalidPropertyName_process_processingResultShouldBeEmpty() {
		assertTrue(processor.process(Stream.empty(), INVALID_PROPERTY_NAME).isEmpty());
	}

	@Test
	void oneElementStreamValidPropertyName_process_processingResultShouldContainOneElement() {
		var usa = Country.builder("USA", Continent.NORTH_AMERICA).capital("Washington").population(250000000)
				.square(6361952.20).build();
		var uk = Country.builder("Great Britain", Continent.EUROPE).capital("London").population(60000000)
				.square(1745813.01).build();
		Person person = Person.builder("Jack Smith", LocalDate.of(1990, 5, 10)).sex(Sex.MALE).eyeColor(Color.BROWN)
				.hairColor(Color.YELLOW).weight(70).height(190).countryOfOrigin(usa).citizenship(uk)
				.favoriteMeals("apple").build();

		ProcessingResult result = processor.process(Stream.of(person), VALID_PROPERTY_NAME);

		assertFalse(result.isEmpty());
		assertEquals(1, result.size());
		var singleResult = result.iterator().next();
		assertEquals("apple", singleResult.value().toString());
		assertEquals(1, singleResult.count());
	}

	@Test
	void oneElementStreamInvalidPropertyName_process_shouldThrowException() {
		var usa = Country.builder("USA", Continent.NORTH_AMERICA).capital("Washington").population(250000000)
				.square(6361952.20).build();
		var uk = Country.builder("Great Britain", Continent.EUROPE).capital("London").population(60000000)
				.square(1745813.01).build();
		Person person = Person.builder("Jack Smith", LocalDate.of(1990, 5, 10)).sex(Sex.MALE).eyeColor(Color.BROWN)
				.hairColor(Color.YELLOW).weight(70).height(190).countryOfOrigin(usa).citizenship(uk)
				.favoriteMeals("apple,pear,grape").build();

		assertThrows(ProcessingException.class, () -> processor.process(Stream.of(person), INVALID_PROPERTY_NAME));
	}

	@Test
	void manyElementStreamValidPropertyName_process_processingResultShouldContainOneElement() {
		var usa = Country.builder("USA", Continent.NORTH_AMERICA).capital("Washington").population(250000000)
				.square(6361952.20).build();
		var uk = Country.builder("Great Britain", Continent.EUROPE).capital("London").population(60000000)
				.square(1745813.01).build();
		Person jackSmith = Person.builder("Jack Smith", LocalDate.of(1990, 5, 10)).sex(Sex.MALE).eyeColor(Color.BROWN)
				.hairColor(Color.YELLOW).weight(70).height(190).countryOfOrigin(usa).citizenship(uk)
				.favoriteMeals("apple,pear,grape").build();
		Person jennyWatson = Person.builder("Jenny Whatson", LocalDate.of(1990, 5, 10)).sex(Sex.FEMALE)
				.eyeColor(Color.BROWN).hairColor(Color.YELLOW).weight(70).height(190).countryOfOrigin(usa)
				.citizenship(uk).favoriteMeals("apple,pear").build();
		Person robertPerry = Person.builder("Robert Perry", LocalDate.of(1990, 5, 10)).sex(Sex.MALE)
				.eyeColor(Color.BROWN).hairColor(Color.YELLOW).weight(70).height(190).countryOfOrigin(usa)
				.citizenship(uk).favoriteMeals("apple").build();

		ProcessingResult result = processor.process(Stream.of(jackSmith, jennyWatson, robertPerry),
				VALID_PROPERTY_NAME);

		assertFalse(result.isEmpty());
		assertEquals(3, result.size());
		var i = result.iterator();
		var firstResult = i.next();
		assertEquals("apple", firstResult.value().toString());
		assertEquals(3, firstResult.count());
		var secondResult = i.next();
		assertEquals("pear", secondResult.value().toString());
		assertEquals(2, secondResult.count());
		var thirdResult = i.next();
		assertEquals("grape", thirdResult.value().toString());
		assertEquals(1, thirdResult.count());
	}

}
