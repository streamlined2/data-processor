package com.streamlined.dataprocessor.entity;

import java.time.LocalDate;
import java.util.Objects;

public class Person implements Entity<String> {

	public enum Sex {
		FEMALE, MALE
	}

	public enum Color {
		GRAY, GREEN, BLUE, BLACK, BROWN, RED, YELLOW
	}

	private String name;
	private LocalDate birthday;
	private Sex sex;
	private Color eyeColor;
	private Color hairColor;
	private double weight;
	private double height;
	private Country countryOfOrigin;
	private Country citizenship;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (Objects.isNull(name) || name.isBlank()) {
			throw new IllegalArgumentException("Person name should not be blank");
		}
		this.name = name;
	}

	public LocalDate getBirthday() {
		return birthday;
	}

	public void setBirthDay(LocalDate birthday) {
		if (Objects.isNull(birthday) || LocalDate.now().isBefore(birthday)) {
			throw new IllegalArgumentException("Person birthday should be non-null and belong to past");
		}
		this.birthday = birthday;
	}

	public Sex getSex() {
		return sex;
	}

	public void setSex(Sex sex) {
		if (Objects.isNull(sex)) {
			throw new IllegalArgumentException("Person sex should not be null");
		}
		this.sex = sex;
	}

	public Color getEyeColor() {
		return eyeColor;
	}

	public void setEyeColor(Color eyeColor) {
		if (Objects.isNull(eyeColor)) {
			throw new IllegalArgumentException("Person eye color should not be null");
		}
		this.eyeColor = eyeColor;
	}

	public Color getHairColor() {
		return hairColor;
	}

	public void setHairColor(Color hairColor) {
		if (Objects.isNull(hairColor)) {
			throw new IllegalArgumentException("Person hair color should not be null");
		}
		this.hairColor = hairColor;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		if (weight < 0) {
			throw new IllegalArgumentException("Person weight should be positive value");
		}
		this.weight = weight;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		if (height < 0) {
			throw new IllegalArgumentException("Person height should be positive value");
		}
		this.height = height;
	}

	public Country getCountryOfOrigin() {
		return countryOfOrigin;
	}

	public void setCountryOfOrigin(Country countryOfOrigin) {
		if (Objects.isNull(countryOfOrigin)) {
			throw new IllegalArgumentException("Country of origin should not be null");
		}
		this.countryOfOrigin = countryOfOrigin;
	}

	public Country getCitizenship() {
		return citizenship;
	}

	public void setCitizenship(Country citizenship) {
		if (Objects.isNull(citizenship)) {
			throw new IllegalArgumentException("Country of citizenship should not be null");
		}
		this.citizenship = citizenship;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Person person) {
			return Objects.equals(name, person.name) && Objects.equals(birthday, person.birthday);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, birthday);
	}

	@Override
	public String toString() {
		return "%s (birthday %tF, sex %s, eye color %s, hair color %s, weight %.2f, height %.2f, country of origin [%s], citizenship [%s])"
				.formatted(name, birthday, sex.toString(), eyeColor.toString(), hairColor.toString(), weight, height,
						countryOfOrigin.toString(), citizenship.toString());
	}

	@Override
	public String getPrimaryKey() {
		return String.join(":", name, Objects.toString(birthday));
	}

}
