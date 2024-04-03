package com.streamlined.dataprocessor.entity;

import java.util.Objects;

public class Country implements Entity<String> {

	public enum Continent {
		AFRICA, ASIA, EUROPE, NORTH_AMERICA, SOUTH_AMERICA, ANTARCTICA, AUSTRALIA
	}

	private String name;
	private Continent continent;
	private String capital;
	private int population;
	private double square;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (Objects.isNull(name) || name.isBlank()) {
			throw new IllegalArgumentException("Country name should not be blank");
		}
		this.name = name;
	}

	public Continent getContinent() {
		return continent;
	}

	public void setContinent(Continent continent) {
		if (Objects.isNull(continent)) {
			throw new IllegalArgumentException("Country continent should not be null");
		}
		this.continent = continent;
	}

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		if (Objects.isNull(capital) || capital.isBlank()) {
			throw new IllegalArgumentException("Country capital should not be blank");
		}
		this.capital = capital;
	}

	public int getPopulation() {
		return population;
	}

	public void setPopulation(int population) {
		if (population < 0) {
			throw new IllegalArgumentException("Country population should be positive value");
		}
		this.population = population;
	}

	public double getSquare() {
		return square;
	}

	public void setSquare(double square) {
		if (square < 0) {
			throw new IllegalArgumentException("Country square should be positive value");
		}
		this.square = square;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Country country) {
			return Objects.equals(name, country.name);
		}
		return false;
	}

	@Override
	public String toString() {
		return "%s (%s): capital %s, population %d, square %.2f".formatted(name, continent.toString(), capital,
				population, square);
	}

	@Override
	public String getPrimaryKey() {
		return getName();
	}

}
