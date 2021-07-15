/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.signature;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.security.GeneralSecurityException;
import java.util.Objects;

/**
 * Wraps a {@link GeneralSecurityException} with an unchecked exception.
 */
public class UncheckedSecurityException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * @throws NullPointerException
     *             if the cause is {@code null}
     */
    public UncheckedSecurityException(final String message, final GeneralSecurityException cause) {
        super(message, Objects.requireNonNull(cause));
    }

    /**
     * @throws NullPointerException
     *             if the cause is {@code null}
     */
    public UncheckedSecurityException(final GeneralSecurityException cause) {
        super(Objects.requireNonNull(cause));
    }

    /**
     * @return the {@code GeneralSecurityException} wrapped by this exception.
     */
    @Override
    public synchronized GeneralSecurityException getCause() {
        return (GeneralSecurityException) super.getCause();
    }

    /**
     * @throws InvalidObjectException
     *             if the object is invalid or has a cause that is not a
     *             {@code GeneralSecurityException}
     */
    private void readObject(final ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        final Throwable cause = super.getCause();
        if (!(cause instanceof GeneralSecurityException)) {
            throw new InvalidObjectException("Cause must be a GeneralSecurityException");
        }
    }

}
