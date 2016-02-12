/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.oslp.exceptions;

public class NotSupportedException extends Exception {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 7315668727470308659L;

    private static final String MESSAGE = "Not Supported for the Elster Protocol";

    public NotSupportedException() {
        super(MESSAGE);
    }
}
