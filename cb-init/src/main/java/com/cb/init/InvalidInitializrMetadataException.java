package com.cb.init;


@SuppressWarnings("serial")
public class InvalidInitializrMetadataException extends RuntimeException {

	public InvalidInitializrMetadataException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidInitializrMetadataException(String message) {
		super(message);
	}
}
