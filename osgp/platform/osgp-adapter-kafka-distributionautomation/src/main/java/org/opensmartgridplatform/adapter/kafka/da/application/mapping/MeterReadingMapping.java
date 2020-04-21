/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.opensmartgridplatform.adapter.kafka.da.avro.IntervalBlock;
import org.opensmartgridplatform.adapter.kafka.da.avro.IntervalReading;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeterReading;
import org.opensmartgridplatform.adapter.kafka.da.avro.ReadingType;
import org.opensmartgridplatform.adapter.kafka.da.avro.UsagePoint;
import org.opensmartgridplatform.adapter.kafka.da.avro.ValuesInterval;
import org.opensmartgridplatform.domain.da.measurements.Measurement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.domain.da.measurements.elements.FloatMeasurementElement;
import org.opensmartgridplatform.domain.da.measurements.elements.TimestampMeasurementElement;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * Class for mapping MeasurementReport to MeterReading
 *
 * The class will be removed once a different AVRO message format is defined.
 */
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
        final ReadingType readingType = this.getReadingType(identification);
        final IntervalBlock intervalBlock = new IntervalBlock(readingType, intervalReadings);

        final LocalDateTime startTime = LocalDate.now(ZoneOffset.UTC)
                .with(TemporalAdjusters.firstDayOfYear())
                .atStartOfDay();
        final Long start = intervalReadings.stream()
                .map(IntervalReading::getTimeStamp)
                .min(Long::compareTo)
                .orElse(startTime.toInstant(ZoneOffset.UTC).toEpochMilli());

        final LocalDateTime endTime = startTime.plusYears(1);
        final Long end = intervalReadings.stream()
                .map(IntervalReading::getTimeStamp)
                .max(Long::compareTo)
                .orElse(endTime.toInstant(ZoneOffset.UTC).toEpochMilli());

        final ValuesInterval valuesInterval = new ValuesInterval(start, end);

        final UsagePoint usagePoint = new UsagePoint(this.getUsagePoint(identification));
        return new MeterReading(valuesInterval, null, identification, usagePoint, Arrays.asList(intervalBlock));
    }

    private ReadingType getReadingType(final String identification) {
        if (identification.contains("Power")) {
            return new ReadingType("10s", "energy", "M", "W", "Energy MW en 10s", null);
        } else {
            return new ReadingType("60s", "temperature", "none", "degC", "Temperature transformateur °C en 60s", null);
        }
    }

    private CharSequence getUsagePoint(final String identification) {
        String usagePoint = "Substation 1";
        if (identification.contains("TFR1")) {
            usagePoint += " PT 1";
        }
        if (identification.contains("TFR2")) {
            usagePoint += " PT 2";
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
