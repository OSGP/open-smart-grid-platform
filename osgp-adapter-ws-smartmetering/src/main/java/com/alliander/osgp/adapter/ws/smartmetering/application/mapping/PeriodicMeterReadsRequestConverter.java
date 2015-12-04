/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.mapping;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ObjectFactory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData;

public class PeriodicMeterReadsRequestConverter
        extends
        BidirectionalConverter<PeriodicMeterReadsRequestData, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsRequestConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData convertTo(
            final PeriodicMeterReadsRequestData source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData> destinationType) {

        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData data = source
                .isGas() ? new ObjectFactory().createPeriodicMeterReadsGasRequest() : new ObjectFactory()
                .createPeriodicMeterReadsRequest();

        data.setDeviceIdentification(source.getDeviceIdentification());

        try {
            data.setBeginDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    new DateTime(source.getBeginDate()).toGregorianCalendar()));
            data.setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                    new DateTime(source.getEndDate()).toGregorianCalendar()));
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("problem converting date to xmlgergoriancalendat", e);
        }
        data.setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        return data;
    }

    @Override
    public PeriodicMeterReadsRequestData convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData source,
            final Type<PeriodicMeterReadsRequestData> destinationType) {

        return new PeriodicMeterReadsRequestData(source.getDeviceIdentification(),
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source.getPeriodType()
                        .name()), source.getBeginDate().toGregorianCalendar().getTime(), source.getEndDate()
                        .toGregorianCalendar().getTime(), source instanceof PeriodicMeterReadsGasRequest);
    }

}
