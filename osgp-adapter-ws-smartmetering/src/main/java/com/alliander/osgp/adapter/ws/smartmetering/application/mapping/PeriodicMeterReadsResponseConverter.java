/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.MeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.OsgpMeterValue;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;

public class PeriodicMeterReadsResponseConverter
extends
CustomConverter<PeriodicMeterReadsContainer, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsResponseConverter.class);

    @Override
    public PeriodicMeterReadsResponse convert(final PeriodicMeterReadsContainer source,
            final Type<? extends PeriodicMeterReadsResponse> destinationType) {
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = new PeriodicMeterReadsResponse();
        periodicMeterReadsResponse.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        final List<PeriodicMeterReads> periodicMeterReads = periodicMeterReadsResponse.getPeriodicMeterReads();
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads m : source
                .getPeriodicMeterReads()) {
            periodicMeterReads.add(this.convert(m));
        }
        return periodicMeterReadsResponse;
    }

    private PeriodicMeterReads convert(
            final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads source) {
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
        meterReads.setActiveEnergyImport(this.getMeterValue(source.getActiveEnergyImport()));
        meterReads.setActiveEnergyExport(this.getMeterValue(source.getActiveEnergyExport()));
        meterReads.setActiveEnergyImportTariffOne(this.getMeterValue(source.getActiveEnergyImportTariffOne()));
        meterReads.setActiveEnergyImportTariffTwo(this.getMeterValue(source.getActiveEnergyImportTariffTwo()));
        meterReads.setActiveEnergyExportTariffOne(this.getMeterValue(source.getActiveEnergyExportTariffOne()));
        meterReads.setActiveEnergyExportTariffTwo(this.getMeterValue(source.getActiveEnergyExportTariffTwo()));
        meterReads.setAmrProfileStatusCode(amrProfileStatusCode);
        return meterReads;
    }

    private MeterValue getMeterValue(final OsgpMeterValue source) {
        return this.mapperFacade.map(source, MeterValue.class);
    }
}
