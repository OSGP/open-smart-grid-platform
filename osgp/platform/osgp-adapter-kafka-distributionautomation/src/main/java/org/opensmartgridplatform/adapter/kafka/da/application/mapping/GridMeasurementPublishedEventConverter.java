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
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
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
 * Class for mapping String containing a measurement or congestion
 * to GridMeasurementPublishedEvent
 */
/**
 * measurement: ean_code; voltage_L1; voltage_L2; voltage_L3; current_in_L1;
 * current_in_L2; current_in_L3; current_returned_L1; current_returned_L2;
 * current_returned_L3;
 */
/**
 * congestion: ean_code; current_in_L1; current_in_L2; current_in_L3;
 * current_returned_L1; current_returned_L2; current_returned_L3;
 *
 */
public class GridMeasurementPublishedEventConverter extends CustomConverter<String, GridMeasurementPublishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridMeasurementPublishedEventConverter.class);

    @Override
    public GridMeasurementPublishedEvent convert(final String source,
            final Type<? extends GridMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        final String[] values = source.split(";");
        if (values.length != 7 && values.length != 10) {
            LOGGER.error("String '{}' does not have the expected amount of fields, abandoning conversion", source);
            return null;
        }
        final List<Analog> measurements = new ArrayList<>();

        int startIndexCurrent = 1;
        int endIndexCurrent = 7;
        final String eanCode = values[0];
        if (values.length == 10) {
            for (int index = 1; index < 4; index++) {
                measurements.add(new Analog(eanCode, UUID.randomUUID().toString(), AccumulationKind.none,
                        MeasuringPeriodKind.none, PhaseCode.none, UnitMultiplier.none, UnitSymbol.V,
                        new ArrayList<Name>(),
                        Arrays.asList(new AnalogValue(Float.valueOf(values[index]), null, null))));
            }
            startIndexCurrent = 4;
            endIndexCurrent = 10;
        }

        for (int index = startIndexCurrent; index < endIndexCurrent; index++) {
            measurements.add(new Analog(eanCode, UUID.randomUUID().toString(), AccumulationKind.none,
                    MeasuringPeriodKind.none, PhaseCode.none, UnitMultiplier.none, UnitSymbol.A, new ArrayList<Name>(),
                    Arrays.asList(new AnalogValue(Float.valueOf(values[index]), null, null))));
        }

        final PowerSystemResource powerSystemResource = new PowerSystemResource(eanCode, UUID.randomUUID().toString(),
                new ArrayList<Name>());
        final long createdDateTime = System.currentTimeMillis();
        return new GridMeasurementPublishedEvent(createdDateTime, eanCode, UUID.randomUUID().toString(),
                "GridMeasurementPublishedEvent", new ArrayList<Name>(), powerSystemResource, measurements);
    }

}
