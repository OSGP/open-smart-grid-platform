/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.application.mapping;

import java.util.List;

import org.joda.time.DateTime;

import org.opensmartgridplatform.dto.valueobjects.MeterTypeDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageDataDto;
import org.opensmartgridplatform.dto.valueobjects.PsldDataDto;
import org.opensmartgridplatform.dto.valueobjects.RelayDataDto;
import org.opensmartgridplatform.dto.valueobjects.SsldDataDto;
import org.opensmartgridplatform.oslp.Oslp;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class PowerUsageDataConverter extends BidirectionalConverter<PowerUsageDataDto, Oslp.PowerUsageData> {

    public PowerUsageDataConverter() {
        // Empty constructor.
    }

    @Override
    public Oslp.PowerUsageData convertTo(final PowerUsageDataDto source,
            final Type<Oslp.PowerUsageData> destinationType, final MappingContext context) {

        // Check the input parameter.
        if (source == null) {
            return null;
        }

        // Create builder for Oslp.PowerUsageData and set the values from
        // source.
        final Oslp.PowerUsageData.Builder powerUsageDataBuilder = Oslp.PowerUsageData.newBuilder();
        powerUsageDataBuilder.setActualConsumedPower((int) source.getActualConsumedPower());
        powerUsageDataBuilder.setMeterType(this.mapperFacade.map(source.getMeterType(), Oslp.MeterType.class));
        powerUsageDataBuilder.setRecordTime(this.mapperFacade.map(source.getRecordTime(), String.class));
        powerUsageDataBuilder.setTotalConsumedEnergy(source.getTotalConsumedEnergy());

        // Check SsldData.
        if (source.getSsldData() != null) {
            // Get SsldData.
            final SsldDataDto ssldData = source.getSsldData();

            // Create builder for Oslp.SsldData and set the values from source.
            final Oslp.SsldData.Builder ssldDataBuilder = Oslp.SsldData.newBuilder();
            ssldDataBuilder.setActualCurrent1(ssldData.getActualCurrent1());
            ssldDataBuilder.setActualCurrent2(ssldData.getActualCurrent2());
            ssldDataBuilder.setActualCurrent3(ssldData.getActualCurrent3());
            ssldDataBuilder.setActualPower1(ssldData.getActualPower1());
            ssldDataBuilder.setActualPower2(ssldData.getActualPower2());
            ssldDataBuilder.setActualPower3(ssldData.getActualPower3());
            ssldDataBuilder.setAveragePowerFactor1(ssldData.getAveragePowerFactor1());
            ssldDataBuilder.setAveragePowerFactor2(ssldData.getAveragePowerFactor2());
            ssldDataBuilder.setAveragePowerFactor3(ssldData.getAveragePowerFactor3());

            // Check RelayData list.
            if (ssldData.getRelayData() != null) {
                // Map the RelayData list and add to Oslp.SsldData builder.
                final List<Oslp.RelayData> list = this.mapperFacade.mapAsList(ssldData.getRelayData(),
                        Oslp.RelayData.class);
                ssldDataBuilder.addAllRelayData(list);
            }

            // Set Oslp.SsldData instance in Oslp.PowerUsageData builder.
            powerUsageDataBuilder.setSsldData(ssldDataBuilder.build());
        }

        // Check PsldData.
        if (source.getPsldData() != null) {
            final Oslp.PsldData.Builder psldDataBuilder = Oslp.PsldData.newBuilder();
            psldDataBuilder.setTotalLightingHours(source.getPsldData().getTotalLightingHours());

            // Set Oslp.PsldData instance in Oslp.PowerUsageData builder.
            powerUsageDataBuilder.setPsldData(psldDataBuilder.build());
        }

        return powerUsageDataBuilder.build();
    }

    @Override
    public PowerUsageDataDto convertFrom(final Oslp.PowerUsageData source,
            final Type<PowerUsageDataDto> destinationType, final MappingContext context) {

        // Check the input parameter.
        if (source == null) {
            return null;
        }

        // Parse the time stamp.
        final DateTime recordTime = this.mapperFacade.map(source.getRecordTime(), DateTime.class);

        // Get the other values.
        final MeterTypeDto meterType = MeterTypeDto.valueOf(source.getMeterType().name());
        final long totalConsumedEnergy = source.getTotalConsumedEnergy();
        final long actualConsumedPower = source.getActualConsumedPower();

        // Construct PowerUsageData instance.
        final PowerUsageDataDto powerUsageData = new PowerUsageDataDto(recordTime, meterType, totalConsumedEnergy,
                actualConsumedPower);

        // Check SsldData.
        if (source.getSsldData() != null) {
            final Oslp.SsldData oslpSsldData = source.getSsldData();

            // Map the RelayData list.
            final List<RelayDataDto> list = this.mapperFacade.mapAsList(oslpSsldData.getRelayDataList(),
                    RelayDataDto.class);

            // Construct SsldData instance using the RelayData list and the
            // other values.
            final SsldDataDto ssldData = SsldDataDto.newBuilder().withActualCurrent1(oslpSsldData.getActualCurrent1())
                    .withActualCurrent2(oslpSsldData.getActualCurrent2())
                    .withActualCurrent3(oslpSsldData.getActualCurrent3())
                    .withActualPower1(oslpSsldData.getActualPower1()).withActualPower2(oslpSsldData.getActualPower2())
                    .withActualPower3(oslpSsldData.getActualPower3())
                    .withAveragePowerFactor1(oslpSsldData.getAveragePowerFactor1())
                    .withAveragePowerFactor2(oslpSsldData.getAveragePowerFactor2())
                    .withAveragePowerFactor3(oslpSsldData.getAveragePowerFactor3()).withRelayData(list).build();
            // Set SsldData instance in the PowerUsageData instance.
            powerUsageData.setSsldData(ssldData);
        }

        // Check PsldData.
        if (source.getPsldData() != null) {
            final Oslp.PsldData oslpPsldData = source.getPsldData();

            // Construct PsldData instance using the value.
            final PsldDataDto psldData = new PsldDataDto(oslpPsldData.getTotalLightingHours());

            // Set PsldData instance in the PowerUsageData instance.
            powerUsageData.setPsldData(psldData);
        }

        return powerUsageData;
    }
}
