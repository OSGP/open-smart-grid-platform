/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.GMeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGas;

public class ActualMeterReadsGasConverter
extends
CustomConverter<ActualMeterReadsGas, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsGasConverter.class);

    @Override
    public ActualMeterReadsGasResponse convert(final ActualMeterReadsGas source,
            final Type<? extends ActualMeterReadsGasResponse> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponse destination = new ObjectFactory()
        .createActualMeterReadsGasResponse();

        final GregorianCalendar c = new GregorianCalendar();
        c.setTime(source.getLogTime());
        XMLGregorianCalendar convertedDate;
        try {
            convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            destination.setLogTime(convertedDate);
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
            convertedDate = null;
        }
        c.setTime(source.getCaptureTime());
        try {
            convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            destination.setCaptureTime(convertedDate);
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
            convertedDate = null;
        }

        destination.setConsumption(this.mapperFacade.map(source.getConsumption(), GMeterValue.class));
        if (destination.getConsumption() != null
                && !destination.getConsumption().getUnit().value().equals(source.getOsgpUnit().name())) {
            throw new IllegalStateException(String.format("unit %s in destionation differs from unit %s in source",
                    destination.getConsumption().getUnit(), source.getOsgpUnit()));
        }

        return destination;
    }

}
