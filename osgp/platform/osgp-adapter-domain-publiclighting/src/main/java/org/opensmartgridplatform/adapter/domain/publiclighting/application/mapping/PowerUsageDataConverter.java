/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.mapping;

import java.util.List;

import org.joda.time.DateTime;

import org.opensmartgridplatform.domain.core.valueobjects.MeterType;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsageData;
import org.opensmartgridplatform.domain.core.valueobjects.PsldData;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsage;
import org.opensmartgridplatform.domain.core.valueobjects.RelayData;
import org.opensmartgridplatform.domain.core.valueobjects.SsldData;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class PowerUsageDataConverter
        extends BidirectionalConverter<org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto, PowerUsageData> {

    @Override
    public PowerUsageData convertTo(final org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto source,
            final Type<PowerUsageData> destinationType, final MappingContext context) {

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
            final org.opensmartgridplatform.dto.valueobjects.SsldDataDto ssldData = source.getSsldData();

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

            ssldDataCopy = new SsldData(new PowerUsage(actualCurrent1, actualPower1, averagePowerFactor1),
                    new PowerUsage(actualCurrent2, actualPower2, averagePowerFactor2),
                    new PowerUsage(actualCurrent3, actualPower3, averagePowerFactor3), relayData);
        }

        PsldData psldDataCopy = null;

        // Check PsldData.
        if (source.getPsldData() != null) {
            final org.opensmartgridplatform.dto.valueobjects.PsldDataDto psldData = source.getPsldData();

            psldDataCopy = new PsldData(psldData.getTotalLightingHours());
        }

        final PowerUsageData powerUsageData = new PowerUsageData(recordTime, meterType, totalConsumedEnergy,
                actualConsumedPower);
        powerUsageData.setPsldData(psldDataCopy);
        powerUsageData.setSsldData(ssldDataCopy);

        return powerUsageData;
    }

    @Override
    public org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto convertFrom(final PowerUsageData source,
            final Type<org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto> destinationType,
            final MappingContext context) {

        // Check the input parameter.
        if (source == null) {
            return null;
        }

        final Long actualConsumedPower = source.getActualConsumedPower();
        final org.opensmartgridplatform.dto.valueobjects.MeterTypeDto meterType = this.mapperFacade.map(source.getMeterType(),
                org.opensmartgridplatform.dto.valueobjects.MeterTypeDto.class);
        final DateTime recordTime = source.getRecordTime();
        final Long totalConsumedEnergy = source.getTotalConsumedEnergy();

        org.opensmartgridplatform.dto.valueobjects.SsldDataDto ssldDataCopy = null;

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

            List<org.opensmartgridplatform.dto.valueobjects.RelayDataDto> relayData = null;

            // Check RelayData list.
            if (ssldData.getRelayData() != null) {
                relayData = this.mapperFacade.mapAsList(ssldData.getRelayData(),
                        org.opensmartgridplatform.dto.valueobjects.RelayDataDto.class);
            }

            ssldDataCopy = org.opensmartgridplatform.dto.valueobjects.SsldDataDto.newBuilder()
                    .withActualCurrent1(actualCurrent1).withActualCurrent2(actualCurrent2)
                    .withActualCurrent3(actualCurrent3).withActualPower1(actualPower1).withActualPower2(actualPower2)
                    .withActualPower3(actualPower3).withAveragePowerFactor1(averagePowerFactor1)
                    .withAveragePowerFactor2(averagePowerFactor2).withAveragePowerFactor3(averagePowerFactor3)
                    .withRelayData(relayData).build();
        }

        org.opensmartgridplatform.dto.valueobjects.PsldDataDto psldDataCopy = null;

        // Check PsldData.
        if (source.getPsldData() != null) {
            final PsldData psldData = source.getPsldData();

            psldDataCopy = new org.opensmartgridplatform.dto.valueobjects.PsldDataDto(psldData.getTotalLightingHours());
        }

        final org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto powerUsageData = new org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto(
                recordTime, meterType, totalConsumedEnergy, actualConsumedPower);
        powerUsageData.setPsldData(psldDataCopy);
        powerUsageData.setSsldData(ssldDataCopy);

        return powerUsageData;
    }
}
