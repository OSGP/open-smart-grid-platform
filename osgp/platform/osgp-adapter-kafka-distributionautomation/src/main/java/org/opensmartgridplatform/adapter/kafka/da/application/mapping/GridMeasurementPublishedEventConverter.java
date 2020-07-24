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
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * Class for mapping String containing a simple measurement or ls peak shaving
 * measurement to GridMeasurementPublishedEvent
 * <p>
 * simple measurement: ean_code; voltage_L1; voltage_L2; voltage_L3;
 * current_in_L1; current_in_L2; current_in_L3; current_returned_L1;
 * current_returned_L2; current_returned_L3;
 * <p>
 * ls peak shaving measurement: ean_code + the values of
 * LsPeakShavingMeasurementType seperated by semicolons.
 */
public class GridMeasurementPublishedEventConverter extends CustomConverter<String, GridMeasurementPublishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GridMeasurementPublishedEventConverter.class);

    private static final int SIMPLE_END_INDEX = 10;

    @Override
    public GridMeasurementPublishedEvent convert(final String source,
            final Type<? extends GridMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        StringArrayToAnalogList stringArrayToAnalogList = null;
        final String[] values = source.split(";");
        if (values.length == SIMPLE_END_INDEX) {
            stringArrayToAnalogList = new SimpleStringToAnalogList();
        } else if (values.length == LsPeakShavingMeasurementType.getNumberOfElements() + 1) {
            stringArrayToAnalogList = new LsMeasurementMessageToAnalogList();
        } else {
            LOGGER.error("String '{}' does not have the expected amount of fields, abandoning conversion", source);
            return null;
        }

        final String eanCode = values[0];
        final PowerSystemResource powerSystemResource = new PowerSystemResource(eanCode, UUID.randomUUID().toString(),
                new ArrayList<Name>());
        final long createdDateTime = System.currentTimeMillis();
        return new GridMeasurementPublishedEvent(createdDateTime, eanCode, UUID.randomUUID().toString(),
                "GridMeasurementPublishedEvent", new ArrayList<Name>(), powerSystemResource,
                stringArrayToAnalogList.convertToAnalogList(values));
    }

}
