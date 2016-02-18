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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.EMeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads;

public class PeriodicMeterReadsConverter
extends
CustomConverter<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsConverter.class);

    @Override
    public PeriodicMeterReads convert(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads source,
            final Type<? extends PeriodicMeterReads> destinationType) {
        final PeriodicMeterReads meterReads = new PeriodicMeterReads();
        final GregorianCalendar c = new GregorianCalendar();
        c.setTime(source.getLogTime());
        XMLGregorianCalendar convertedDate;
        try {
            convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
            convertedDate = null;
        }

        final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(source.getAmrProfileStatusCode(),
                AmrProfileStatusCode.class);

        meterReads.setLogTime(convertedDate);
        meterReads.setActiveEnergyImport(this.electricityMeterValueFromDouble(source.getActiveEnergyImport()));
        meterReads.setActiveEnergyExport(this.electricityMeterValueFromDouble(source.getActiveEnergyExport()));
        meterReads.setActiveEnergyImportTariffOne(this.electricityMeterValueFromDouble(source
                .getActiveEnergyImportTariffOne()));
        meterReads.setActiveEnergyImportTariffTwo(this.electricityMeterValueFromDouble(source
                .getActiveEnergyImportTariffTwo()));
        meterReads.setActiveEnergyExportTariffOne(this.electricityMeterValueFromDouble(source
                .getActiveEnergyExportTariffOne()));
        meterReads.setActiveEnergyExportTariffTwo(this.electricityMeterValueFromDouble(source
                .getActiveEnergyExportTariffTwo()));
        meterReads.setAmrProfileStatusCode(amrProfileStatusCode);
        return meterReads;
    }

    private EMeterValue electricityMeterValueFromDouble(final Double d) {
        return this.mapperFacade.map(d, EMeterValue.class);
    }
}
