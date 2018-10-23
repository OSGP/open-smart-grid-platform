/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class PowerUsageData implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -4500816713354211228L;

    private DateTime recordTime;
    private MeterType meterType;
    private long totalConsumedEnergy;
    private long actualConsumedPower;

    private PsldData psldData;
    private SsldData ssldData;

    public PowerUsageData(final DateTime recordTime, final MeterType meterType, final long totalConsumedEnergy,
            final long actualConsumedPower) {
        this.recordTime = recordTime;

        if (meterType == null) {
            throw new IllegalArgumentException("Meter type is null.");
        } else {
            this.meterType = meterType;
        }

        this.totalConsumedEnergy = totalConsumedEnergy;
        this.actualConsumedPower = actualConsumedPower;
    }

    public DateTime getRecordTime() {
        return this.recordTime;
    }

    public MeterType getMeterType() {
        return this.meterType;
    }

    public long getTotalConsumedEnergy() {
        return this.totalConsumedEnergy;
    }

    public long getActualConsumedPower() {
        return this.actualConsumedPower;
    }

    public PsldData getPsldData() {
        return this.psldData;
    }

    public SsldData getSsldData() {
        return this.ssldData;
    }

    public void setPsldData(final PsldData psldData) {
        this.psldData = psldData;
    }

    public void setSsldData(final SsldData ssldData) {
        this.ssldData = ssldData;
    }
}
