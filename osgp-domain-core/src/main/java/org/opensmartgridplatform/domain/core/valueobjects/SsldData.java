/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.util.ArrayList;
import java.util.List;

public class SsldData implements java.io.Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 4785387346353649099L;

    private int actualCurrent1;
    private int actualCurrent2;
    private int actualCurrent3;
    private int actualPower1;
    private int actualPower2;
    private int actualPower3;
    private int averagePowerFactor1;
    private int averagePowerFactor2;
    private int averagePowerFactor3;
    private List<RelayData> relayData = new ArrayList<RelayData>();

    public SsldData(final int actualCurrent1, final int actualCurrent2, final int actualCurrent3,
            final int actualPower1, final int actualPower2, final int actualPower3, final int averagePowerFactor1,
            final int averagePowerFactor2, final int averagePowerFactor3, final List<RelayData> relayData) {
        this.actualCurrent1 = actualCurrent1;
        this.actualCurrent2 = actualCurrent2;
        this.actualCurrent3 = actualCurrent3;
        this.actualPower1 = actualPower1;
        this.actualPower2 = actualPower2;
        this.actualPower3 = actualPower3;
        this.averagePowerFactor1 = averagePowerFactor1;
        this.averagePowerFactor2 = averagePowerFactor2;
        this.averagePowerFactor3 = averagePowerFactor3;
        this.relayData = relayData;
    }

    public int getActualCurrent1() {
        return this.actualCurrent1;
    }

    public int getActualCurrent2() {
        return this.actualCurrent2;
    }

    public int getActualCurrent3() {
        return this.actualCurrent3;
    }

    public int getActualPower1() {
        return this.actualPower1;
    }

    public int getActualPower2() {
        return this.actualPower2;
    }

    public int getActualPower3() {
        return this.actualPower3;
    }

    public int getAveragePowerFactor1() {
        return this.averagePowerFactor1;
    }

    public int getAveragePowerFactor2() {
        return this.averagePowerFactor2;
    }

    public int getAveragePowerFactor3() {
        return this.averagePowerFactor3;
    }

    public List<RelayData> getRelayData() {
        return this.relayData;
    }
}
