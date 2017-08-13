package com.cb.generator.exception;

/**
 * @author Chandan
 *
 */
@SuppressWarnings("serial")
public class InitializrException extends RuntimeException {

	public InitializrException(String message, Throwable cause) {
		super(message, cause);
	}

	public InitializrException(String message) {
		super(message);
	}

}

