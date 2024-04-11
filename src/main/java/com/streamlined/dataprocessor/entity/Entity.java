package com.streamlined.dataprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Common base interface for every entity class
 * @param <T> primary key type
 */
public interface Entity<T> {

	/**
	 * Primary key of the entity
	 * @return value of primary key
	 */
	@JsonIgnore
	T getPrimaryKey();

}
