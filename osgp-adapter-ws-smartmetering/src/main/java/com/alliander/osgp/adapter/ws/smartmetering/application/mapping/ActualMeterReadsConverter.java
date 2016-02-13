/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import static com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper.eFromDouble;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;

public class ActualMeterReadsConverter
        extends
        BidirectionalConverter<ActualMeterReads, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse convertTo(
            final ActualMeterReads source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse destination = new ObjectFactory()
                .createActualMeterReadsResponse();

        final GregorianCalendar c = new GregorianCalendar();
        c.setTime(source.getLogTime());
        XMLGregorianCalendar convertedDate;
        try {
            convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
            convertedDate = null;
        }

        destination.setLogTime(convertedDate);
        destination.setActiveEnergyImport(eFromDouble(source.getActiveEnergyImport()));
        destination.setActiveEnergyExport(eFromDouble(source.getActiveEnergyExport()));
        destination.setActiveEnergyExportTariffOne(eFromDouble(source.getActiveEnergyExportTariffOne()));
        destination.setActiveEnergyExportTariffTwo(eFromDouble(source.getActiveEnergyExportTariffTwo()));
        destination.setActiveEnergyImportTariffOne(eFromDouble(source.getActiveEnergyImportTariffOne()));
        destination.setActiveEnergyImportTariffTwo(eFromDouble(source.getActiveEnergyImportTariffTwo()));

        return destination;
    }

    @Override
    public ActualMeterReads convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse source,
            final Type<ActualMeterReads> destinationType) {

        throw new IllegalStateException(
                "mapping a response meant for the ws layer to a response from the platform layer should not be necessary");
    }
}
