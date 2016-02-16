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
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.EMeterValue;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReads;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;

public class PeriodicMeterReadsConverter
extends
CustomConverter<PeriodicMeterReadContainer, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsConverter.class);

    @Override
    public PeriodicMeterReadsResponse convert(final PeriodicMeterReadContainer source,
            final Type<? extends PeriodicMeterReadsResponse> destinationType) {
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
            meterReads.setActiveEnergyImport(this.eFromDouble(m.getActiveEnergyImport()));
            if (!meterReads.getActiveEnergyImport().getUnit().name().equals(source.getOsgpUnit().name())) {
                throw new IllegalStateException(String.format("unit %s in destionation differs from unit %s in source",
                        meterReads.getActiveEnergyImport().getUnit(), source.getOsgpUnit()));
            }
            meterReads.setActiveEnergyExport(this.eFromDouble(m.getActiveEnergyExport()));
            meterReads.setActiveEnergyImportTariffOne(this.eFromDouble(m.getActiveEnergyImportTariffOne()));
            meterReads.setActiveEnergyImportTariffTwo(this.eFromDouble(m.getActiveEnergyImportTariffTwo()));
            meterReads.setActiveEnergyExportTariffOne(this.eFromDouble(m.getActiveEnergyExportTariffOne()));
            meterReads.setActiveEnergyExportTariffTwo(this.eFromDouble(m.getActiveEnergyExportTariffTwo()));
            meterReads.setAmrProfileStatusCode(amrProfileStatusCode);
            periodicMeterReads.add(meterReads);
        }
        return periodicMeterReadsResponse;
    }

    private EMeterValue eFromDouble(final Double d) {
        return this.mapperFacade.map(d, EMeterValue.class);
    }
}
