/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.signing.server.domain.exceptions;

public class SigningServerException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -1461900304884232355L;

    public SigningServerException() {
        super();
    }

    public SigningServerException(final String message) {
        super(message);
    }

    public SigningServerException(final String message, final Throwable t) {
        super(message, t);
    }
}
