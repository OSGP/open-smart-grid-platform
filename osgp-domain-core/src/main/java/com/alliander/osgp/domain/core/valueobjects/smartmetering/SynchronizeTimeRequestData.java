/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

import com.alliander.osgp.shared.exceptionhandling.FunctionalException;

public class SynchronizeTimeRequestData implements Serializable, ActionRequest {

    private static final long serialVersionUID = -7197837365579237374L;

    private Date date;

    public Date getDate() {
        return this.date;
    }

    public void setDate(final Date date) {
        this.date = date;
    }

    @Override
    public void validate() throws FunctionalException {
        // No validation needed
    }
}