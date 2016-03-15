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
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;

public class ActualMeterReadsConverter
        extends
        CustomConverter<MeterReads, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActualMeterReadsConverter.class);

    @Override
    public ActualMeterReadsResponse convert(final MeterReads source,
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
        destination.setActiveEnergyImport(this.getMeterValue(source.getActiveEnergyImport()));
        destination.setActiveEnergyExport(this.getMeterValue(source.getActiveEnergyExport()));
        destination.setActiveEnergyExportTariffOne(this.getMeterValue(source.getActiveEnergyExportTariffOne()));
        destination.setActiveEnergyExportTariffTwo(this.getMeterValue(source.getActiveEnergyExportTariffTwo()));
        destination.setActiveEnergyImportTariffOne(this.getMeterValue(source.getActiveEnergyImportTariffOne()));
        destination.setActiveEnergyImportTariffTwo(this.getMeterValue(source.getActiveEnergyImportTariffTwo()));

        return destination;
    }

    private MeterValue getMeterValue(final OsgpMeterValue source) {
        return this.mapperFacade.map(source, MeterValue.class);
    }

}
