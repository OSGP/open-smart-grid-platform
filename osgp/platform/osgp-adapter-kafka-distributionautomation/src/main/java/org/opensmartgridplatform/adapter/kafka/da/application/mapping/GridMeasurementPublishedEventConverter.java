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

import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GridMeasurementPublishedEvent convert(final String source,
            final Type<? extends GridMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        StringArrayToAnalogList stringArrayToAnalogList = null;
        LOGGER.info("Source string: {}", source);

        try {
            final Payload payload = this.objectMapper.readValue(source, Payload.class);
            LOGGER.info("Gisnr payload: {}", payload.getGisnr());
        } catch (final JsonMappingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final JsonProcessingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        final String[] eancode = {
                source.substring(ordinalIndexOf(source, "\"", 3) + 1, ordinalIndexOf(source, "\"", 4)) };
        LOGGER.info("Eancode: {}", eancode[0]);

        final String[] data = source.substring(ordinalIndexOf(source, "[", 2) + 1, source.indexOf("]")).split(",");
        LOGGER.info("Data length {} and data {}", data.length, Arrays.toString(data));

        final String[] values = new String[data.length + eancode.length];
        System.arraycopy(eancode, 0, values, 0, eancode.length);
        System.arraycopy(data, 0, values, eancode.length, data.length);

        LOGGER.info("Values length: {} and values: {}", values.length, Arrays.toString(values));

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

    private static int ordinalIndexOf(final String str, final String substr, int n) {
        int pos = str.indexOf(substr);
        while (--n > 0 && pos != -1) {
            pos = str.indexOf(substr, pos + 1);
        }
        return pos;
    }

    private class Payload {

        private final String gisnr;
        private final String feeder;
        private final String D;
        private final String data;

        public Payload(final String gisnr, final String feeder, final String d, final String data) {
            this.gisnr = gisnr;
            this.feeder = feeder;
            this.D = d;
            this.data = data;
        }

        public String getGisnr() {
            return this.gisnr;
        }

        public String getFeeder() {
            return this.feeder;
        }

        public String getD() {
            return this.D;
        }

        public String getData() {
            return this.data;
        }
    }
}
