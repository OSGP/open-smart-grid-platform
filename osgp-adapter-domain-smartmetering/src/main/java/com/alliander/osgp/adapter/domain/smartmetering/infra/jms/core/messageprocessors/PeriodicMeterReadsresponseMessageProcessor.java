/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component("domainSmartMeteringPeriodicMeterReadsResponseMessageProcessor")
public class PeriodicMeterReadsresponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringMonitoringService")
    private MonitoringService monitoringService;

    protected PeriodicMeterReadsresponseMessageProcessor() {
        super(DeviceFunction.REQUEST_PERIODIC_METER_DATA);
    }

    @Override
    protected void handleMessage(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessage responseMessage,
            final OsgpException osgpException) {

        if (responseMessage.getDataObject() instanceof PeriodicMeterReadsContainer) {
            final PeriodicMeterReadsContainer periodicMeterReadsContainer = (PeriodicMeterReadsContainer) responseMessage
                    .getDataObject();

            this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, responseMessage.getResult(), osgpException,
                    periodicMeterReadsContainer);
        } else {
            final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas = (PeriodicMeterReadsContainerGas) responseMessage
                    .getDataObject();

            this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, responseMessage.getResult(), osgpException,
                    periodicMeterReadsContainerGas);
        }
    }
}
