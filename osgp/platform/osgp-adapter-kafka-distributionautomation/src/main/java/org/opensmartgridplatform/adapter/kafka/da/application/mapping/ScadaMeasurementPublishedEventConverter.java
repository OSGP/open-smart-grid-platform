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
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.infra.mqtt.in.ScadaMeasurementPayload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.data.scadameasurementpublishedevent.BaseVoltage;
import com.alliander.data.scadameasurementpublishedevent.ConductingEquipment;
import com.alliander.data.scadameasurementpublishedevent.Name;
import com.alliander.data.scadameasurementpublishedevent.NameType;
import com.alliander.data.scadameasurementpublishedevent.ScadaMeasurementPublishedEvent;
import com.alliander.data.scadameasurementpublishedevent.UnitMultiplier;
import com.alliander.data.scadameasurementpublishedevent.UnitSymbol;
import com.alliander.data.scadameasurementpublishedevent.Voltage;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

public class ScadaMeasurementPublishedEventConverter
        extends CustomConverter<ScadaMeasurementPayload, ScadaMeasurementPublishedEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScadaMeasurementPublishedEventConverter.class);

    private static final float LOW_VOLTAGE_NOMINAL = 0.4f;

    @Override
    public ScadaMeasurementPublishedEvent convert(final ScadaMeasurementPayload source,
            final Type<? extends ScadaMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        if (source == null || source.getData() == null) {
            LOGGER.error("The payload is null or has no measurement values");
            return null;
        }
        StringArrayToAnalogList stringArrayToAnalogList = null;

        final String[] measurementValues = source.getData();

        if (measurementValues.length == LsPeakShavingMeasurementType.getNumberOfElements()) {
            stringArrayToAnalogList = new LsMeasurementMessageToAnalogList();
        } else if (measurementValues.length == LsPeakShavingMetaMeasurementType.getNumberOfElements()) {
            stringArrayToAnalogList = new LsMetaMeasurementMessageToAnalogList();
        } else {
            LOGGER.error(
                    "Measurement values does not have the expected amount of fields. Expecting: {}, actual: {}. Payload: {}.",
                    LsPeakShavingMeasurementType.getNumberOfElements(), measurementValues.length, source);
            return null;
        }

        LOGGER.debug("Values length: {} and values: {}", measurementValues.length, Arrays.toString(measurementValues));

        return new ScadaMeasurementPublishedEvent(stringArrayToAnalogList.convertToAnalogList(measurementValues),
                this.createPowerSystemResource(source), source.getCreatedUtcSeconds() * 1000l,
                source.getSubstationIdentification(), UUID.randomUUID().toString());
    }

    private ConductingEquipment createPowerSystemResource(final ScadaMeasurementPayload source) {
        final ArrayList<Name> names = new ArrayList<>();
        names.add(new Name(new NameType("gisbehuizingnummer"), source.getSubstationIdentification()));
        names.add(new Name(new NameType("msr naam"), source.getSubstationName()));
        names.add(new Name(new NameType("bay positie"), source.getFeeder()));
        names.add(new Name(new NameType("bay identificatie"), source.getBayIdentification()));
        final Voltage voltage = new Voltage(UnitMultiplier.k, UnitSymbol.V, LOW_VOLTAGE_NOMINAL);
        return new ConductingEquipment(new BaseVoltage("LS", voltage), names);
    }

}
