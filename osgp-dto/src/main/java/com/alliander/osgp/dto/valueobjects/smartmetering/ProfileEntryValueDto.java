/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProfileEntryValueDto implements Serializable {

    private static final long serialVersionUID = 2123390296585369209L;

    private final Serializable value;

    public ProfileEntryValueDto(final String value) {
        this.value = value;
    }

    public ProfileEntryValueDto(final Date value) {
        this.value = value;
    }

    public ProfileEntryValueDto(final Long value) {
        this.value = value;
    }

    public ProfileEntryValueDto(final BigDecimal value) {
        this.value = value;
    }

    public Serializable getValue() {
        return this.value;
    }

}
