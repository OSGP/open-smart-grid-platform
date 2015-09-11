/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.valueobjects;

import java.io.Serializable;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class RelayMap implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -8997468148053647259L;

    @NotNull
    @Min(1)
    @Max(6)
    private final Integer index;

    @NotNull
    @Min(1)
    @Max(255)
    private final Integer address;

    @NotNull
    private RelayType relayType;

    public RelayMap(final Integer index, final Integer address, final RelayType relayType) {
        this.index = index;
        this.address = address;
        this.relayType = relayType;
    }

    public Integer getIndex() {
        return this.index;
    }

    public Integer getAddress() {
        return this.address;
    }

    public RelayType getRelayType() {
        return this.relayType;
    }

    public void changeRelayType(final RelayType relayType) {
        this.relayType = relayType;
    }
}
