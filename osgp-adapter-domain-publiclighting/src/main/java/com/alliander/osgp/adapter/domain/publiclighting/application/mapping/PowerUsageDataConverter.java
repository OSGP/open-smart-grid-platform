/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.publiclighting.application.mapping;

import java.util.List;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;

import com.alliander.osgp.domain.core.valueobjects.MeterType;
import com.alliander.osgp.domain.core.valueobjects.PowerUsageData;
import com.alliander.osgp.domain.core.valueobjects.PsldData;
import com.alliander.osgp.domain.core.valueobjects.RelayData;
import com.alliander.osgp.domain.core.valueobjects.SsldData;

public class PowerUsageDataConverter extends
        BidirectionalConverter<com.alliander.osgp.dto.valueobjects.PowerUsageDataDto, PowerUsageData> {

    @Override
    public PowerUsageData convertTo(final com.alliander.osgp.dto.valueobjects.PowerUsageDataDto source,
            final Type<PowerUsageData> destinationType) {

        // Check the input parameter.
        if (source == null) {
            return null;
        }

        final Long actualConsumedPower = source.getActualConsumedPower();
        final MeterType meterType = this.mapperFacade.map(source.getMeterType(), MeterType.class);
        final DateTime recordTime = source.getRecordTime();
        final Long totalConsumedEnergy = source.getTotalConsumedEnergy();

        SsldData ssldDataCopy = null;

        // Check SsldData.
        if (source.getSsldData() != null) {
            // Get SsldData.
            final com.alliander.osgp.dto.valueobjects.SsldDataDto ssldData = source.getSsldData();

            final int actualCurrent1 = ssldData.getActualCurrent1();
            final int actualCurrent2 = ssldData.getActualCurrent2();
            final int actualCurrent3 = ssldData.getActualCurrent3();
            final int actualPower1 = ssldData.getActualPower1();
            final int actualPower2 = ssldData.getActualPower2();
            final int actualPower3 = ssldData.getActualPower3();
            final int averagePowerFactor1 = ssldData.getAveragePowerFactor1();
            final int averagePowerFactor2 = ssldData.getAveragePowerFactor2();
            final int averagePowerFactor3 = ssldData.getAveragePowerFactor3();

            List<RelayData> relayData = null;

            // Check RelayData list.
            if (ssldData.getRelayData() != null) {
                relayData = this.mapperFacade.mapAsList(ssldData.getRelayData(), RelayData.class);
            }

            ssldDataCopy = new SsldData(actualCurrent1, actualCurrent2, actualCurrent3, actualPower1, actualPower2,
                    actualPower3, averagePowerFactor1, averagePowerFactor2, averagePowerFactor3, relayData);
        }

        PsldData psldDataCopy = null;

        // Check PsldData.
        if (source.getPsldData() != null) {
            final com.alliander.osgp.dto.valueobjects.PsldDataDto psldData = source.getPsldData();

            psldDataCopy = new PsldData(psldData.getTotalLightingHours());
        }

        final PowerUsageData powerUsageData = new PowerUsageData(recordTime, meterType, totalConsumedEnergy,
                actualConsumedPower);
        powerUsageData.setPsldData(psldDataCopy);
        powerUsageData.setSsldData(ssldDataCopy);

        return powerUsageData;
    }

    @Override
    public com.alliander.osgp.dto.valueobjects.PowerUsageDataDto convertFrom(final PowerUsageData source,
            final Type<com.alliander.osgp.dto.valueobjects.PowerUsageDataDto> destinationType) {

        // Check the input parameter.
        if (source == null) {
            return null;
        }

        final Long actualConsumedPower = source.getActualConsumedPower();
        final com.alliander.osgp.dto.valueobjects.MeterTypeDto meterType = this.mapperFacade.map(source.getMeterType(),
                com.alliander.osgp.dto.valueobjects.MeterTypeDto.class);
        final DateTime recordTime = source.getRecordTime();
        final Long totalConsumedEnergy = source.getTotalConsumedEnergy();

        com.alliander.osgp.dto.valueobjects.SsldDataDto ssldDataCopy = null;

        // Check SsldData.
        if (source.getSsldData() != null) {
            // Get SsldData.
            final SsldData ssldData = source.getSsldData();

            final int actualCurrent1 = ssldData.getActualCurrent1();
            final int actualCurrent2 = ssldData.getActualCurrent2();
            final int actualCurrent3 = ssldData.getActualCurrent3();
            final int actualPower1 = ssldData.getActualPower1();
            final int actualPower2 = ssldData.getActualPower2();
            final int actualPower3 = ssldData.getActualPower3();
            final int averagePowerFactor1 = ssldData.getAveragePowerFactor1();
            final int averagePowerFactor2 = ssldData.getAveragePowerFactor2();
            final int averagePowerFactor3 = ssldData.getAveragePowerFactor3();

            List<com.alliander.osgp.dto.valueobjects.RelayDataDto> relayData = null;

            // Check RelayData list.
            if (ssldData.getRelayData() != null) {
                relayData = this.mapperFacade.mapAsList(ssldData.getRelayData(),
                        com.alliander.osgp.dto.valueobjects.RelayDataDto.class);
            }

            ssldDataCopy = new com.alliander.osgp.dto.valueobjects.SsldDataDto(actualCurrent1, actualCurrent2,
                    actualCurrent3, actualPower1, actualPower2, actualPower3, averagePowerFactor1, averagePowerFactor2,
                    averagePowerFactor3, relayData);
        }

        com.alliander.osgp.dto.valueobjects.PsldDataDto psldDataCopy = null;

        // Check PsldData.
        if (source.getPsldData() != null) {
            final PsldData psldData = source.getPsldData();

            psldDataCopy = new com.alliander.osgp.dto.valueobjects.PsldDataDto(psldData.getTotalLightingHours());
        }

        final com.alliander.osgp.dto.valueobjects.PowerUsageDataDto powerUsageData = new com.alliander.osgp.dto.valueobjects.PowerUsageDataDto(
                recordTime, meterType, totalConsumedEnergy, actualConsumedPower);
        powerUsageData.setPsldData(psldDataCopy);
        powerUsageData.setSsldData(ssldDataCopy);

        return powerUsageData;
    }
}
