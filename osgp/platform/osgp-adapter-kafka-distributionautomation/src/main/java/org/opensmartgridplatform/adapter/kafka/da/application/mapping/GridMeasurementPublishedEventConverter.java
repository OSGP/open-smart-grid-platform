/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.avro.AccumulationKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Analog;
import org.opensmartgridplatform.adapter.kafka.da.avro.AnalogValue;
import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeasuringPeriodKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.PhaseCode;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * Class for mapping String containing a measurement to
 * GridMeasurementPublishedEvent
 * <p>
 * measurement: ean_code; voltage_L1; voltage_L2; voltage_L3; current_in_L1;
 * current_in_L2; current_in_L3; current_returned_L1; current_returned_L2;
 * current_returned_L3;
 */
public class GridMeasurementPublishedEventConverter extends CustomConverter<String, GridMeasurementPublishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridMeasurementPublishedEventConverter.class);

    private static final int VOLTAGE_START_INDEX = 1;
    private static final int CURRENT_START_INDEX = 4;
    private static final int CURRENT_RETURNED_START_INDEX = 7;
    private static final int END_INDEX = 10;

    @Override
    public GridMeasurementPublishedEvent convert(final String source,
            final Type<? extends GridMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        final String[] values = source.split(";");
        if (values.length != END_INDEX) {
            LOGGER.error("String '{}' does not have the expected amount of fields, abandoning conversion", source);
            return null;
        }
        final List<Analog> measurements = new ArrayList<>();

        final String eanCode = values[0];
        for (int index = VOLTAGE_START_INDEX; index < CURRENT_START_INDEX; index++) {
            final String description = eanCode + ":voltage_L" + index;
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]), UnitSymbol.V));
        }

        for (int index = CURRENT_START_INDEX; index < CURRENT_RETURNED_START_INDEX; index++) {
            final String description = eanCode + ":current_in_L" + (index - CURRENT_START_INDEX + 1);
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]), UnitSymbol.A));
        }

        for (int index = CURRENT_RETURNED_START_INDEX; index < END_INDEX; index++) {
            final String description = eanCode + ":current_returned_L" + (index - CURRENT_RETURNED_START_INDEX + 1);
            measurements.add(this.createAnalog(description, Float.valueOf(values[index]), UnitSymbol.A));
        }

        final PowerSystemResource powerSystemResource = new PowerSystemResource(eanCode, UUID.randomUUID().toString(),
                new ArrayList<Name>());
        final long createdDateTime = System.currentTimeMillis();
        return new GridMeasurementPublishedEvent(createdDateTime, eanCode, UUID.randomUUID().toString(),
                "GridMeasurementPublishedEvent", new ArrayList<Name>(), powerSystemResource, measurements);
    }

    private Analog createAnalog(final String description, final Float value, final UnitSymbol unitSymbol) {
        return new Analog(description, UUID.randomUUID().toString(), AccumulationKind.none, MeasuringPeriodKind.none,
                PhaseCode.none, UnitMultiplier.none, unitSymbol, new ArrayList<Name>(),
                Arrays.asList(new AnalogValue(value, null, null)));
    }

}
