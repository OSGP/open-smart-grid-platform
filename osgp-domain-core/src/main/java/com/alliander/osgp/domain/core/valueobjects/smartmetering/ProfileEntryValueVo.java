/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class ProfileEntryValueVo implements Serializable {

    private static final long serialVersionUID = 991045734132231709L;

    private final Serializable value;

    public ProfileEntryValueVo(String value) {
        this.value = value;
    }

    public ProfileEntryValueVo(Date value) {
        this.value = value;
    }

    public ProfileEntryValueVo(Long value) {
        this.value = value;
    }

    public ProfileEntryValueVo(BigDecimal value) {
        this.value = value;
    }

    public Serializable getValue() {
        return this.value;
    }

}