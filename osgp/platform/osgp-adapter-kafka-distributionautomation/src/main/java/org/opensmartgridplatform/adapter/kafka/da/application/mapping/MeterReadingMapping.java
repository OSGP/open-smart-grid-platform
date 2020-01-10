/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.opensmartgridplatform.adapter.kafka.IntervalBlock;
import org.opensmartgridplatform.adapter.kafka.IntervalReading;
import org.opensmartgridplatform.adapter.kafka.MeterReading;
import org.opensmartgridplatform.adapter.kafka.ReadingType;
import org.opensmartgridplatform.adapter.kafka.UsagePoint;
import org.opensmartgridplatform.adapter.kafka.ValuesInterval;
import org.opensmartgridplatform.domain.da.measurements.Measurement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class MeterReadingMapping extends CustomConverter<MeasurementReport, MeterReading> {

    @Override
    public MeterReading convert(final MeasurementReport source, final Type<? extends MeterReading> destinationType,
            final MappingContext mappingContext) {

        final List<MeasurementElement> measurementElements = source.getMeasurementGroups()
                .stream()
                .map(MeasurementGroup::getMeasurements)
                .flatMap(List::stream)
                .map(Measurement::getMeasurementElements)
                .flatMap(List::stream)
                .collect(Collectors.toList());

        final List<IntervalReading> intervalReadings = Arrays.asList(this.getIntervalReading(measurementElements));

        final String identification = source.getMeasurementGroups().get(0).getIdentification();
        final ReadingType readingType = getReadingType(identification);
        final IntervalBlock intervalBlock = new IntervalBlock(readingType, intervalReadings);

        final Long start = intervalReadings.stream()
                .map(IntervalReading::getTimeStamp)
                .min(Long::compareTo)
                .orElse(dateStringToEpoch("2020-01-13 00:00:00"));
        final Long end = intervalReadings.stream()
                .map(IntervalReading::getTimeStamp)
                .max(Long::compareTo)
                .orElse(dateStringToEpoch("2020-01-16 00:00:00"));
        final ValuesInterval valuesInterval = new ValuesInterval(start, end);

        final UsagePoint usagePoint = new UsagePoint(this.getUsagePoint(identification));
        return new MeterReading(valuesInterval, null, identification, usagePoint, Arrays.asList(intervalBlock));

    }

    private ReadingType getReadingType(String identification) {
        if (identification.contains("TotW")) {
            return new ReadingType("10s", "energy", "M", "W", "Energy MW en 10s", null);
        } else {
            return new ReadingType("60s", "temperature", "none", "degC", "Temperature transformateur °C en 60s", null);
        }
    }

    private static long dateStringToEpoch(final String dateString) {
        final LocalDateTime localDateTime = LocalDateTime.parse(dateString,
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    private CharSequence getUsagePoint(final String identification) {
        String usagePoint = "Substation 1";
        if (identification.contains("TFR1")) {
            usagePoint += "PT 1";
        }
        if (identification.contains("TFR2")) {
            usagePoint += "PT 2";
        }
        return usagePoint;
    }

    private IntervalReading getIntervalReading(final List<MeasurementElement> measurementElements) {
        final IntervalReading reading = new IntervalReading();
        for (final MeasurementElement measurementElement : measurementElements) {
            if (measurementElement instanceof TimestampMeasurementElement) {
                final TimestampMeasurementElement timestamp = (TimestampMeasurementElement) measurementElement;
                reading.setTimeStamp(timestamp.getValue());
            }
            if (measurementElement instanceof FloatMeasurementElement) {
                final FloatMeasurementElement floatMeasurement = (FloatMeasurementElement) measurementElement;
                reading.setValue(floatMeasurement.getValue().toString());
            }
        }
        return reading;
    }

}
