package com.alliander.osgp.oslp;

@SuppressWarnings("serial")
public class UnknownOslpDecodingStateException extends Exception {

	private static final String message = "Unknown OSLP decoding state: %1$s";
	
	public UnknownOslpDecodingStateException(String unknownState) {
		super(String.format(message, unknownState));
	}
}
