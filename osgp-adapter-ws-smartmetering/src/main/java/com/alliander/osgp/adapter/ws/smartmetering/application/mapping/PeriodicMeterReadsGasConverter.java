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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGas;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;

public class PeriodicMeterReadsGasConverter
extends
BidirectionalConverter<PeriodicMeterReadsContainerGas, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponse> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsGasConverter.class);

    @Override
    public PeriodicMeterReadsGasResponse convertTo(final PeriodicMeterReadsContainerGas source,
            final Type<PeriodicMeterReadsGasResponse> destinationType) {
        final PeriodicMeterReadsGasResponse periodicMeterReadsResponse = new PeriodicMeterReadsGasResponse();
        periodicMeterReadsResponse.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        final List<PeriodicMeterReadsGas> periodicMeterReads = periodicMeterReadsResponse.getPeriodicMeterReadsGas();
        for (final MeterReadsGas m : source.getMeterReadsGas()) {
            final PeriodicMeterReadsGas meterReads = new PeriodicMeterReadsGas();
            final GregorianCalendar c = new GregorianCalendar();
            c.setTime(m.getLogTime());
            XMLGregorianCalendar convertedDate;
            try {
                convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (final DatatypeConfigurationException e) {
                LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
                convertedDate = null;
            }
            meterReads.setLogTime(convertedDate);
            meterReads.setConsumption(m.getConsumption());
            c.setTime(m.getCaptureTime());
            try {
                convertedDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            } catch (final DatatypeConfigurationException e) {
                LOGGER.error("JAXB mapping: An error occured while converting calendar types.", e);
                convertedDate = null;
            }
            meterReads.setCaptureTime(convertedDate);
            periodicMeterReads.add(meterReads);
        }
        return periodicMeterReadsResponse;
    }

    @Override
    public PeriodicMeterReadsContainerGas convertFrom(final PeriodicMeterReadsGasResponse source,
            final Type<PeriodicMeterReadsContainerGas> destinationType) {
        final List<MeterReadsGas> meterReads = new ArrayList<MeterReadsGas>(source.getPeriodicMeterReadsGas().size());
        for (final PeriodicMeterReadsGas reads : source.getPeriodicMeterReadsGas()) {
            meterReads.add(new MeterReadsGas(reads.getLogTime().toGregorianCalendar().getTime(),
                    reads.getConsumption(), reads.getCaptureTime().toGregorianCalendar().getTime()));
        }
        return new PeriodicMeterReadsContainerGas(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), meterReads);
    }

}
