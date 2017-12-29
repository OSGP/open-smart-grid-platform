/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SsldDataDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -3380158030038178362L;

    private int actualCurrent1;
    private int actualCurrent2;
    private int actualCurrent3;
    private int actualPower1;
    private int actualPower2;
    private int actualPower3;
    private int averagePowerFactor1;
    private int averagePowerFactor2;
    private int averagePowerFactor3;
    private List<RelayDataDto> relayData = new ArrayList<>();

    private SsldDataDto(final Builder builder) {
        this.actualCurrent1 = builder.actualCurrent1;
        this.actualCurrent2 = builder.actualCurrent2;
        this.actualCurrent3 = builder.actualCurrent3;
        this.actualPower1 = builder.actualPower1;
        this.actualPower2 = builder.actualPower2;
        this.actualPower3 = builder.actualPower3;
        this.averagePowerFactor1 = builder.averagePowerFactor1;
        this.averagePowerFactor2 = builder.averagePowerFactor2;
        this.averagePowerFactor3 = builder.averagePowerFactor3;
        this.relayData = builder.relayData;
    }

    public static class Builder {

        private int actualCurrent1;
        private int actualCurrent2;
        private int actualCurrent3;
        private int actualPower1;
        private int actualPower2;
        private int actualPower3;
        private int averagePowerFactor1;
        private int averagePowerFactor2;
        private int averagePowerFactor3;
        private List<RelayDataDto> relayData = new ArrayList<>();

        public SsldDataDto build() {
            return new SsldDataDto(this);
        }

        public Builder withActualCurrent1(final int actualCurrent1) {
            this.actualCurrent1 = actualCurrent1;
            return this;
        }

        public Builder withActualCurrent2(final int actualCurrent2) {
            this.actualCurrent2 = actualCurrent2;
            return this;
        }

        public Builder withActualCurrent3(final int actualCurrent3) {
            this.actualCurrent3 = actualCurrent3;
            return this;
        }

        public Builder withActualPower1(final int actualPower1) {
            this.actualPower1 = actualPower1;
            return this;
        }

        public Builder withActualPower2(final int actualPower2) {
            this.actualPower2 = actualPower2;
            return this;
        }

        public Builder withActualPower3(final int actualPower3) {
            this.actualPower3 = actualPower3;
            return this;
        }

        public Builder withAveragePowerFactor1(final int averagePowerFactor1) {
            this.averagePowerFactor1 = averagePowerFactor1;
            return this;
        }

        public Builder withAveragePowerFactor2(final int averagePowerFactor2) {
            this.averagePowerFactor2 = averagePowerFactor2;
            return this;
        }

        public Builder withAveragePowerFactor3(final int averagePowerFactor3) {
            this.averagePowerFactor3 = averagePowerFactor3;
            return this;
        }

        public Builder withRelayData(final List<RelayDataDto> relayData) {
            this.relayData = relayData;
            return this;
        }
    }

    public static Builder newBuilder() {
        return new Builder();
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

    public List<RelayDataDto> getRelayData() {
        return this.relayData;
    }
}
