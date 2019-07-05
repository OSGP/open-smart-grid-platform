/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec61850;

public class OperationFailedException extends Exception {

    private static final long serialVersionUID = 1L;

    public OperationFailedException(final String message, final Throwable inner) {
        super(message, inner);
    }

}
