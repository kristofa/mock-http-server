package com.harlap.test.http;

public class UnsatisfiedExpectationException extends RuntimeException {
	private static final long serialVersionUID = -6003072239642243697L;

	public UnsatisfiedExpectationException() {
		super();
	}

	public UnsatisfiedExpectationException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnsatisfiedExpectationException(String message) {
		super(message);
	}

	public UnsatisfiedExpectationException(Throwable cause) {
		super(cause);
	}

}
