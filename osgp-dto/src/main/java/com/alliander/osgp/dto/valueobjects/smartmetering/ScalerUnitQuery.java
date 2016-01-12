/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;

/**
 * request scaler and unit for E or GAS meters. This query will cause scaler and
 * unit to be retrieved from a meter and stored in the protocol layer.
 *
 * @author dev
 */
public class ScalerUnitQuery implements Serializable {
    private static final long serialVersionUID = 3751586818507193990L;

    private static final int NOCHANNEL = -1;

    private final int channel;

    public ScalerUnitQuery() {
        this(NOCHANNEL);
    }

    public ScalerUnitQuery(final int channel) {
        this.channel = channel;
    }

    public boolean isMbus() {
        return this.channel > NOCHANNEL;
    }

    public int getChannel() {
        return this.channel;
    }

}
