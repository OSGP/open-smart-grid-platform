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
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;

public class PeriodicMeterReadsRequestConverter
        extends
        BidirectionalConverter<PeriodicMeterReadsQuery, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsRequestConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest convertTo(
            final PeriodicMeterReadsQuery source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest> destinationType) {

        com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest data = source.isGas() ? new PeriodicMeterReadsGasRequest()
                : new PeriodicMeterReadsRequest();
        data.setPeriodicReadsRequestData(new PeriodicReadsRequestData());

        try {
            data.getPeriodicReadsRequestData().setBeginDate(
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(
                            new DateTime(source.getBeginDate()).toGregorianCalendar()));
            data.getPeriodicReadsRequestData().setEndDate(
                    DatatypeFactory.newInstance().newXMLGregorianCalendar(
                            new DateTime(source.getEndDate()).toGregorianCalendar()));
        } catch (final DatatypeConfigurationException e) {
            LOGGER.error("problem converting date to xmlgergoriancalendat", e);
        }
        data.getPeriodicReadsRequestData().setPeriodType(PeriodType.valueOf(source.getPeriodType().name()));
        return data;
    }

    @Override
    public PeriodicMeterReadsQuery convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest source,
            final Type<PeriodicMeterReadsQuery> destinationType) {

        return new PeriodicMeterReadsQuery(
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(source
                        .getPeriodicReadsRequestData().getPeriodType().name()), source.getPeriodicReadsRequestData()
                        .getBeginDate().toGregorianCalendar().getTime(), source.getPeriodicReadsRequestData()
                        .getEndDate().toGregorianCalendar().getTime(), source instanceof PeriodicMeterReadsGasRequest);
    }

}
