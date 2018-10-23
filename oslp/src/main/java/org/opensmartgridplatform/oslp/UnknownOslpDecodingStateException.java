/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.oslp;

@SuppressWarnings("serial")
public class UnknownOslpDecodingStateException extends Exception {

	private static final String MESSAGE = "Unknown OSLP decoding state: %1$s";
	
	public UnknownOslpDecodingStateException(String unknownState) {
		super(String.format(MESSAGE, unknownState));
	}
}
