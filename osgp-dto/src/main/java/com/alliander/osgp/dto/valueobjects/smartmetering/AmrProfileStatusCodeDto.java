/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class AmrProfileStatusCodeDto implements Serializable {

    private static final long serialVersionUID = 2319359505656305783L;

    private final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlags;

    public AmrProfileStatusCodeDto(final Set<AmrProfileStatusCodeFlagDto> amrProfileStatusCodeFlags) {
        this.amrProfileStatusCodeFlags = new TreeSet<AmrProfileStatusCodeFlagDto>(amrProfileStatusCodeFlags);
    }

    @Override
    public String toString() {
        return "AmrProfileStatusCodeFlags[" + this.amrProfileStatusCodeFlags + "]";
    }

    public Set<AmrProfileStatusCodeFlagDto> getAmrProfileStatusCodeFlags() {
        return Collections.unmodifiableSet(this.amrProfileStatusCodeFlags);
    }
}
