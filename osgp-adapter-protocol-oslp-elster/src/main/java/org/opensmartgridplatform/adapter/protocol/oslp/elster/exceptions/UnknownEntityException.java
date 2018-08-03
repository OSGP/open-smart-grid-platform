/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.exceptions;

public class UnknownEntityException extends ProtocolAdapterException {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4937419294803845764L;
    private static final String MESSAGE = "%1$s with id \"%2$s\" could not be found.";

    public UnknownEntityException(final Class<?> entity, final String identification) {
        super(String.format(MESSAGE, entity.getSimpleName(), identification));
    }
}
