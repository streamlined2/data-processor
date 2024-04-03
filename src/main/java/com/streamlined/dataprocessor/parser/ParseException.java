package com.streamlined.dataprocessor.parser;

public class ParseException extends RuntimeException {

	public ParseException(String message) {
		super(message);
	}

	public ParseException(String message, Exception exception) {
		super(message, exception);
	}

}
