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

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReadsRequestConverter
        extends
        BidirectionalConverter<PeriodicMeterReadsRequest, com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeriodicMeterReadsRequestConverter.class);

    @Override
    public com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest convertTo(
            final PeriodicMeterReadsRequest source,
            final Type<com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest periodicMeterReadsRequest = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest();

        periodicMeterReadsRequest.setDeviceIdentification(source.getDeviceIdentification());

        for (final PeriodicMeterReadsRequestData pmrd : source.getPeriodicMeterReadsRequestData()) {

            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData pm = new com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData();
            try {
                pm.setBeginDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        new DateTime(pmrd.getBeginDate()).toGregorianCalendar()));
                pm.setEndDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        new DateTime(pmrd.getEndDate()).toGregorianCalendar()));
            } catch (final DatatypeConfigurationException e) {
                LOGGER.error("problem converting date to xmlgergoriancalendat", e);
            }
            pm.setPeriodType(PeriodType.valueOf(pmrd.getPeriodType().name()));
            periodicMeterReadsRequest.getPeriodicMeterReadsRequestData().add(pm);
        }
        return periodicMeterReadsRequest;
    }

    @Override
    public PeriodicMeterReadsRequest convertFrom(
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest source,
            final Type<PeriodicMeterReadsRequest> destinationType) {

        final List<PeriodicMeterReadsRequestData> periodicMeterReadsRequestData = new ArrayList<>();
        for (final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequestData pmrd : source
                .getPeriodicMeterReadsRequestData()) {

            final PeriodicMeterReadsRequestData pm = new PeriodicMeterReadsRequestData(
                    source.getDeviceIdentification(),
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(pmrd.getPeriodType()
                            .name()), pmrd.getBeginDate().toGregorianCalendar().getTime(), pmrd.getEndDate()
                            .toGregorianCalendar().getTime());
            periodicMeterReadsRequestData.add(pm);
        }
        return new PeriodicMeterReadsRequest(source.getDeviceIdentification(),periodicMeterReadsRequestData);
    }

}
