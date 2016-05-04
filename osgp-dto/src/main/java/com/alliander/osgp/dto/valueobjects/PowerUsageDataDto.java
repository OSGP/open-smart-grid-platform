/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects;

import java.io.Serializable;

import org.joda.time.DateTime;

public class PowerUsageDataDto implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = 7571950922988802598L;

    private DateTime recordTime;
    private MeterTypeDto meterType;
    private long totalConsumedEnergy;
    private long actualConsumedPower;

    private PsldDataDto psldData;
    private SsldDataDto ssldData;

    public PowerUsageDataDto(final DateTime recordTime, final MeterTypeDto meterType, final long totalConsumedEnergy,
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

    public MeterTypeDto getMeterType() {
        return this.meterType;
    }

    public long getTotalConsumedEnergy() {
        return this.totalConsumedEnergy;
    }

    public long getActualConsumedPower() {
        return this.actualConsumedPower;
    }

    public PsldDataDto getPsldData() {
        return this.psldData;
    }

    public SsldDataDto getSsldData() {
        return this.ssldData;
    }

    public void setPsldData(final PsldDataDto psldData) {
        this.psldData = psldData;
    }

    public void setSsldData(final SsldDataDto ssldData) {
        this.ssldData = ssldData;
    }
}
