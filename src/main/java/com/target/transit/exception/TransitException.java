package com.target.transit.exception;

public class TransitException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TransitException() {

	}

	public TransitException(String message) {
		super(message);
	}

}
