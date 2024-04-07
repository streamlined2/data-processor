package com.streamlined.dataprocessor.datagenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.streamlined.dataprocessor.entity.Country;
import com.streamlined.dataprocessor.entity.Country.Continent;
import com.streamlined.dataprocessor.entity.Person;

public class PersonDataGenerator {

	private static final Path RESULT_FILE_DIRECTORY = Path.of("src", "main", "resources", "data");
	private static final String FILE_NAME = "data";
	private static final String FILE_EXTENSION = ".json";
	private static final int PERSON_COUNT = 1_000_000;
	private static final int FILE_COUNT = 100;

	private static final double MIN_WEIGHT = 50;
	private static final double MAX_WEIGHT = 150;
	private static final double MIN_HEIGHT = 120;
	private static final double MAX_HEIGHT = 220;
	private static final int MIN_MEALS_COUNT = 3;
	private static final LocalDate BIRTHDAY_START = LocalDate.of(1970, 1, 1);
	private static final long BIRTHDAY_RANGE = 50 * ChronoUnit.YEARS.getDuration().toDays();

	private final List<Country> countries = List.of(
			Country.builder("Great Britain", Continent.EUROPE).capital("London").population(60000000).square(1745813.01)
					.build(),
			Country.builder("USA", Continent.NORTH_AMERICA).capital("Washington").population(250000000)
					.square(6361952.20).build(),
			Country.builder("Germany", Continent.EUROPE).capital("Berlin").population(75000000).square(1279813.01)
					.build(),
			Country.builder("Canada", Continent.NORTH_AMERICA).capital("Ottawa").population(125000000)
					.square(6345813.01).build(),
			Country.builder("Netherlands", Continent.EUROPE).capital("Amsterdam").population(35000000).square(425853.01)
					.build());

	private final List<String> meals = List.of("apple", "pear", "grape", "banana", "watermelon");

	private final List<String> names = List.of("Charley Thomas", "Jess Newton", "Tom Kent", "Hiram Horton",
			"Jess Burgess", "Perry Gleason", "Nathan Hahn", "Claude Sorensen", "Oliver Elliott", "Eli Summers",
			"Amos Webb", "Cecil Cash", "Guy Nielsen", "Milton Jensen", "Vernon Brady", "Alexander Adams",
			"Clarence Griffith", "Howard Nichols", "Jasper Steiner", "Walter Denton", "Mack Kent", "Hubert Jennings",
			"Alfred Emery", "Martin Ellis", "Oliver Ackerman", "Joseph Crowley", "Wallace Justice", "Eugene Helton",
			"Hugh Waller", "Earl Wallace");

	private final ObjectMapper mapper;
	private final Random random;

	public PersonDataGenerator() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		random = new SecureRandom();
	}

	public void createData(Path resultFileDirectory, int totalNumberOfPersons, int numberOfFiles) {
		prepareResultFileDirectory(resultFileDirectory);
		final int personsPerFile = (int) Math.ceil((double) totalNumberOfPersons / numberOfFiles);
		int numberOfPersons = totalNumberOfPersons;
		for (int fileNumber = 0; fileNumber < numberOfFiles && numberOfPersons > 0; fileNumber++) {
			final int restPersons = Math.min(personsPerFile, numberOfPersons);
			createDataForOneFile(resultFileDirectory, restPersons, fileNumber);
			numberOfPersons -= restPersons;
		}
	}

	private void createDataForOneFile(Path resultFileDirectory, int numberOfPersons, int fileNumber) {
		try (var writer = Files.newBufferedWriter(getResultFile(resultFileDirectory, fileNumber),
				StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE)) {
			List<Person> persons = new ArrayList<>(numberOfPersons);
			for (int k = 0; k < numberOfPersons; k++) {
				persons.add(createPerson());
			}
			mapper.writeValue(writer, persons);
		} catch (IOException e) {
			throw new GeneratorException("Can't write generated data", e);
		}
	}

	private void prepareResultFileDirectory(Path resultFileDirectory) {
		try {
			Files.createDirectories(resultFileDirectory);
		} catch (IOException e) {
			throw new GeneratorException("Can't create directory for data to generate", e);
		}
	}

	private Path getResultFile(Path resultFileDirectory, int fileNumber) {
		return new File(resultFileDirectory.toFile(), FILE_NAME + fileNumber + FILE_EXTENSION).toPath();
	}

	private Person createPerson() {
		return Person.builder(getName(), getBirthday()).sex(getSex()).eyeColor(getColor()).hairColor(getColor())
				.weight(getWeight()).height(getHeight()).countryOfOrigin(getCountry()).citizenship(getCountry())
				.favoriteMeals(getFavoriteMeals()).build();
	}

	private Country getCountry() {
		return countries.get(random.nextInt(countries.size()));
	}

	private Person.Color getColor() {
		return Person.Color.values()[random.nextInt(Person.Color.values().length)];
	}

	private Person.Sex getSex() {
		return Person.Sex.values()[random.nextInt(Person.Sex.values().length)];
	}

	private double getWeight() {
		return MIN_WEIGHT + random.nextDouble(MAX_WEIGHT - MIN_WEIGHT);
	}

	private double getHeight() {
		return MIN_HEIGHT + random.nextDouble(MAX_HEIGHT - MIN_HEIGHT);
	}

	private String getName() {
		return names.get(random.nextInt(names.size()));
	}

	private LocalDate getBirthday() {
		return BIRTHDAY_START.plus(random.nextLong(BIRTHDAY_RANGE), ChronoUnit.DAYS);
	}

	private String getFavoriteMeals() {
		var mealSet = new ArrayList<>(meals);
		Collections.shuffle(mealSet, random);
		int count = MIN_MEALS_COUNT + random.nextInt(meals.size() - MIN_MEALS_COUNT);
		var mealIterator = mealSet.iterator();
		StringBuilder b = new StringBuilder();
		if (count > 0) {
			b.append(mealIterator.next());
			count--;
			while (count > 0) {
				b.append(",").append(mealIterator.next());
				count--;
			}
		}
		return b.toString();
	}
/*
	public static void main(String... args) {
		new PersonDataGenerator().createData(RESULT_FILE_DIRECTORY, PERSON_COUNT, FILE_COUNT);
	}
*/
}
