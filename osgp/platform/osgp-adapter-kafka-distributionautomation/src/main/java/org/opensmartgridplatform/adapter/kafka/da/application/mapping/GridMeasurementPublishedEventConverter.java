/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.mapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import org.opensmartgridplatform.adapter.kafka.da.avro.GridMeasurementPublishedEvent;
import org.opensmartgridplatform.adapter.kafka.da.avro.Name;
import org.opensmartgridplatform.adapter.kafka.da.avro.PowerSystemResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GridMeasurementPublishedEvent convert(final String source,
            final Type<? extends GridMeasurementPublishedEvent> destinationType, final MappingContext mappingContext) {

        LsMeasurementMessageToAnalogList stringArrayToAnalogList = null;
        LOGGER.info("Source string: {}", source);

        try {
            final Payload payload = this.objectMapper.readValue(source, Payload[].class)[0];

            final String[] values = (payload.gisnr + ", " + String.join(", ", payload.data)).split(", ");
            LOGGER.info("Values length: {} and values: {}", values.length, Arrays.toString(values));

            if (values.length == LsPeakShavingMeasurementType.getNumberOfElements() + 1) {
                stringArrayToAnalogList = new LsMeasurementMessageToAnalogList();
            } else {
                LOGGER.error("String '{}' does not have the expected amount of fields, abandoning conversion", source);
                return null;
            }

            final String eanCode = values[0];
            final PowerSystemResource powerSystemResource = new PowerSystemResource(eanCode,
                    UUID.randomUUID().toString(), new ArrayList<Name>());
            final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
            final Date date = dateFormat.parse(payload.date);
            final long createdDateTime = date.getTime();
            LOGGER.info("CreatedDateTime: {}", createdDateTime);
            return new GridMeasurementPublishedEvent(createdDateTime, eanCode, UUID.randomUUID().toString(),
                    "GridMeasurementPublishedEvent", new ArrayList<Name>(), powerSystemResource,
                    stringArrayToAnalogList.convertToAnalogList(values));
        } catch (final JsonMappingException e) {
            LOGGER.error("Caught an error mapping a JSON string to Payload. {}", source, e);
            return null;
        } catch (final JsonProcessingException e) {
            LOGGER.error("Caught an error processing a JSON string to Payload. {}", source, e);
            return null;
        } catch (final ParseException e) {
            LOGGER.error("Date could not be parsed corrrectly. Date format is: yyyy-mm-dd HH:mm:ss, "
                    + "however the provided date was not the correct format. {}", source, e);
            return null;
        }
    }

    private static class Payload {

        private String gisnr;
        private String feeder;
        @JsonAlias({ "D" })
        private String date;
        private String[] data;

        public Payload() {
            super();
        }

        public Payload(final String gisnr, final String feeder, final String date, final String[] data) {
            this.gisnr = gisnr;
            this.feeder = feeder;
            this.date = date;
            this.data = data;
        }

        public String getGisnr() {
            return this.gisnr;
        }

        public String getFeeder() {
            return this.feeder;
        }

        public String getDate() {
            return this.date;
        }

        public String[] getData() {
            return this.data;
        }
    }
}
