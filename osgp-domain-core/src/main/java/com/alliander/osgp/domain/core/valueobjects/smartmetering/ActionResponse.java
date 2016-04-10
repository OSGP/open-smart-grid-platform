/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class ActionResponse implements Serializable {

    private static final long serialVersionUID = -1400608536968152107L;
    private String exception;
    private String resultString;

    public String getException() {
        return this.exception;
    }

    public void setException(final String exception) {
        this.exception = exception;
    }

    public boolean hasException() {
        return this.exception != null;
    }

    public String getResultString() {
        return this.resultString;
    }

    public void setResultString(final String resultString) {
        this.resultString = resultString;

    }
}