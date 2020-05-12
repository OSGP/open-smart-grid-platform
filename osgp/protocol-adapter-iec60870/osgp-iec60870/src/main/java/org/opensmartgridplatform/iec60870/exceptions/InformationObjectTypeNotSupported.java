/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.iec60870.exceptions;

public class InformationObjectTypeNotSupported extends RuntimeException {

    private static final long serialVersionUID = -8763916774984177378L;

    public InformationObjectTypeNotSupported() {
        super();
    }

    public InformationObjectTypeNotSupported(final String message) {
        super(message);
    }

}
