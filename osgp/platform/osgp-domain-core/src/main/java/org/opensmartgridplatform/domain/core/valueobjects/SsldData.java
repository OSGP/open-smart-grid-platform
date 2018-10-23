/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.List;

public class SsldData implements java.io.Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4785387346353649099L;

    private final PowerUsage powerUsage1;
    private final PowerUsage powerUsage2;
    private final PowerUsage powerUsage3;
    private final List<RelayData> relayData;

    public SsldData(final PowerUsage powerUsage1, final PowerUsage powerUsage2, final PowerUsage powerUsage3, final List<RelayData> relayData) {
        this.powerUsage1 = powerUsage1;
        this.powerUsage2 = powerUsage2;
        this.powerUsage3 = powerUsage3;
        this.relayData = relayData;
    }

    public int getActualCurrent1() {
        return this.powerUsage1.getActualCurrent();
    }

    public int getActualCurrent2() {
        return this.powerUsage2.getActualCurrent();
    }

    public int getActualCurrent3() {
        return this.powerUsage3.getActualCurrent();
    }

    public int getActualPower1() {
        return this.powerUsage1.getActualPower();
    }

    public int getActualPower2() {
        return this.powerUsage2.getActualPower();
    }

    public int getActualPower3() {
        return this.powerUsage3.getActualPower();
    }

    public int getAveragePowerFactor1() {
        return this.powerUsage1.getAveragePowerFactor();
    }

    public int getAveragePowerFactor2() {
        return this.powerUsage2.getAveragePowerFactor();
    }

    public int getAveragePowerFactor3() {
        return this.powerUsage3.getAveragePowerFactor();
    }

    public List<RelayData> getRelayData() {
        return this.relayData;
    }
}
