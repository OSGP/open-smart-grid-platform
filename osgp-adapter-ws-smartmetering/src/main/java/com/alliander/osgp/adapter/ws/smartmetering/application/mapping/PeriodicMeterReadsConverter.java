/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AmrProfileStatusCode;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;

public class PeriodicMeterReadsConverter
extends
BidirectionalConverter<PeriodicMeterReadContainer, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsConverter.class);

    @Override
    public PeriodicMeterReadsResponse convertTo(final PeriodicMeterReadContainer source,
            final Type<PeriodicMeterReadsResponse> destinationType) {
        final PeriodicMeterReadsResponse periodicMeterReadsResponse = new PeriodicMeterReadsResponse();
        periodicMeterReadsResponse.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        final List<PeriodicMeterReads> periodicMeterReads = periodicMeterReadsResponse.getPeriodicMeterReads();
        for (final com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads m : source
                .getPeriodicMeterReads()) {
            final PeriodicMeterReads meterReads = new PeriodicMeterReads();
            final GregorianCalendar c = new GregorianCalendar();
            c.setTime(m.getLogTime());
            XMLGregorianCalendar convertedDate;
            try {
                convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (final DatatypeConfigurationException e) {
                LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
                convertedDate = null;
            }

            final AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade.map(m.getAmrProfileStatusCode(),
                    AmrProfileStatusCode.class);

            meterReads.setLogTime(convertedDate);
            meterReads.setActiveEnergyImportTariffOne(m.getActiveEnergyImportTariffOne());
            meterReads.setActiveEnergyImportTariffTwo(m.getActiveEnergyImportTariffTwo());
            meterReads.setActiveEnergyExportTariffOne(m.getActiveEnergyExportTariffOne());
            meterReads.setActiveEnergyExportTariffTwo(m.getActiveEnergyExportTariffTwo());
            meterReads.setAmrProfileStatusCode(amrProfileStatusCode);
            periodicMeterReads.add(meterReads);
        }
        return periodicMeterReadsResponse;
    }

    @Override
    public PeriodicMeterReadContainer convertFrom(final PeriodicMeterReadsResponse source,
            final Type<PeriodicMeterReadContainer> destinationType) {
        final List<com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads> meterReads = new ArrayList<>(
                source.getPeriodicMeterReads().size());
        for (final PeriodicMeterReads reads : source.getPeriodicMeterReads()) {

            final com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode amrProfileStatusCode = this.mapperFacade
                    .map(reads.getAmrProfileStatusCode(),
                            com.alliander.osgp.domain.core.valueobjects.smartmetering.AmrProfileStatusCode.class);

            meterReads.add(new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReads(reads
                    .getLogTime().toGregorianCalendar().getTime(), reads.getActiveEnergyImportTariffOne(), reads
                    .getActiveEnergyImportTariffTwo(), reads.getActiveEnergyExportTariffOne(), reads
                    .getActiveEnergyExportTariffTwo(), amrProfileStatusCode));
        }
        return new PeriodicMeterReadContainer(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), meterReads);
    }
}
