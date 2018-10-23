/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

public class PowerUsage implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 6780427560822971719L;
    private final int actualCurrent;
    private final int actualPower;
    private final int averagePowerFactor;

    public PowerUsage(final int actualCurrent, final int actualPower, final int averagePowerFactor) {
        this.actualCurrent = actualCurrent;
        this.actualPower = actualPower;
        this.averagePowerFactor = averagePowerFactor;
    }

    public int getActualCurrent() {
        return this.actualCurrent;
    }

    public int getActualPower() {
        return this.actualPower;
    }

    public int getAveragePowerFactor() {
        return this.averagePowerFactor;
    }
}
