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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.EMeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReads;

public class ActualMeterReadsConverter
        extends
        CustomConverter<ActualMeterReads, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsConverter.class);

    @Override
    public ActualMeterReadsResponse convert(final ActualMeterReads source,
            final Type<? extends ActualMeterReadsResponse> destinationType) {

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
        destination.setActiveEnergyImport(this.eFromDouble(source.getActiveEnergyImport()));
        // we try to check the unit
        EMeterValue eMeterValue = destination.getActiveEnergyImport();
        if (eMeterValue == null) {
            eMeterValue = destination.getActiveEnergyImportTariffOne();
        }
        if (eMeterValue != null && !eMeterValue.getUnit().value().equals(source.getOsgpUnit().name())) {
            throw new IllegalStateException(String.format("unit %s in destination differs from unit %s in source",
                    eMeterValue.getUnit(), source.getOsgpUnit()));
        }
        destination.setActiveEnergyExport(this.eFromDouble(source.getActiveEnergyExport()));
        destination.setActiveEnergyExportTariffOne(this.eFromDouble(source.getActiveEnergyExportTariffOne()));
        destination.setActiveEnergyExportTariffTwo(this.eFromDouble(source.getActiveEnergyExportTariffTwo()));
        destination.setActiveEnergyImportTariffOne(this.eFromDouble(source.getActiveEnergyImportTariffOne()));
        destination.setActiveEnergyImportTariffTwo(this.eFromDouble(source.getActiveEnergyImportTariffTwo()));

        return destination;
    }

    private EMeterValue eFromDouble(final Double d) {
        return this.mapperFacade.map(d, EMeterValue.class);
    }
}
