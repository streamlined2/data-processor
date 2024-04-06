package com.streamlined.dataprocessor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Entity<T> {

	@JsonIgnore
	T getPrimaryKey();

}
