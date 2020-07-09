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
import java.util.List;
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.avro.AccumulationKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.MeasuringPeriodKind;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PhaseCode;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitMultiplier;
import org.opensmartgridplatform.adapter.kafka.da.avro.UnitSymbol;
import org.opensmartgridplatform.domain.da.measurements.MeasurementGroup;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

/**
 * Class for mapping MeasurementReport to MeterReading
 */
public class GridMeasurementPublishedEventMapping
        extends CustomConverter<MeasurementReport, GridMeasurementPublishedEvent> {

    @Override
    public GridMeasurementPublishedEvent convert(final MeasurementReport source,
            final Type<? extends GridMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        final String identification = source.getMeasurementGroups().get(0).getIdentification();

        final List<org.opensmartgridplatform.adapter.kafka.da.avro.Measurement> measurements = new ArrayList<>();
        for (final MeasurementGroup measurementGroup : source.getMeasurementGroups()) {
            measurements.add(new org.opensmartgridplatform.adapter.kafka.da.avro.Measurement(
                    measurementGroup.getIdentification(), UUID.randomUUID().toString(), AccumulationKind.none,
                    MeasuringPeriodKind.none, PhaseCode.none, UnitMultiplier.none, UnitSymbol.none,
                    new ArrayList<Name>()));
        }

        final PowerSystemResource powerSystemResource = new PowerSystemResource(identification, identification,
                new ArrayList<Name>());
        final long createdDateTime = System.currentTimeMillis();
        return new GridMeasurementPublishedEvent(createdDateTime, identification, UUID.randomUUID().toString(),
                "GridMeasurementPublishedEvent", measurements, new ArrayList<Name>(), powerSystemResource);
    }

}
