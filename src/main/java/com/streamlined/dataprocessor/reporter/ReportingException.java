package com.streamlined.dataprocessor.reporter;

public class ReportingException extends RuntimeException {

	public ReportingException(String message) {
		super(message);
	}

	public ReportingException(String message, Throwable cause) {
		super(message, cause);
	}

}
