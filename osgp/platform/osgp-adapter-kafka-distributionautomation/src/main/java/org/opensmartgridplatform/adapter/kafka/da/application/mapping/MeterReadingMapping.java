/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

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

        // TODO fill readingType with values from the source
        final ReadingType readingType = new ReadingType("10s", "energy", "M", "W", "Energy MW en 10s", null);
        final IntervalBlock intervalBlock = new IntervalBlock(readingType, intervalReadings);

        final Long start = intervalReadings.stream().map(IntervalReading::getTimeStamp).min(Long::compareTo).get();
        final Long end = intervalReadings.stream().map(IntervalReading::getTimeStamp).max(Long::compareTo).get();
        final ValuesInterval valuesInterval = new ValuesInterval(start, end);

        // TODO fill usagePoint with values from the source
        final UsagePoint usagePoint = new UsagePoint("Substation 1 PT 1");
        return new MeterReading(valuesInterval, null, null, usagePoint, Arrays.asList(intervalBlock));

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
