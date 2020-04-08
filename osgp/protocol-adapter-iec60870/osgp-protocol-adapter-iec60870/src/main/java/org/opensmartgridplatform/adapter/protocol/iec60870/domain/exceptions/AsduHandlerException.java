/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.exceptions;

import java.util.function.Supplier;

public class AsduHandlerException extends Exception {

    private static final long serialVersionUID = 1L;

    public AsduHandlerException(final String message) {
        super(message);
    }

    public static Supplier<AsduHandlerException> withMessage(final String message) {
        return () -> new AsduHandlerException(message);
    }
}
