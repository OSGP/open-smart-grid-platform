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
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

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
        } else if (responseMessage.getDataObject() instanceof PeriodicMeterReadsContainerGas) {
            final PeriodicMeterReadsContainerGas periodicMeterReadsContainerGas = (PeriodicMeterReadsContainerGas) responseMessage
                    .getDataObject();

            this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, responseMessage.getResult(), osgpException,
                    periodicMeterReadsContainerGas);
        } else if (responseMessage.getDataObject() instanceof PeriodicMeterReadsQuery) {
            final OsgpException e;
            if (osgpException == null) {
                e = new TechnicalException(ComponentType.DOMAIN_SMART_METERING,
                        "Error retrieving periodic meter reads.", null);
            } else {
                e = osgpException;
            }
            if (((PeriodicMeterReadsQuery) responseMessage.getDataObject()).isGas()) {
                this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification,
                        organisationIdentification, correlationUid, messageType, ResponseMessageResultType.NOT_OK, e,
                        (PeriodicMeterReadsContainerGas) null);
            } else {
                this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification,
                        organisationIdentification, correlationUid, messageType, ResponseMessageResultType.NOT_OK, e,
                        (PeriodicMeterReadsContainer) null);
            }
        } else if (osgpException == null) {
            this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, ResponseMessageResultType.NOT_OK, new TechnicalException(
                            ComponentType.DOMAIN_SMART_METERING, "Error retrieving periodic meter reads.", null),
                            (PeriodicMeterReadsContainer) null);
        } else {
            this.monitoringService.handlePeriodicMeterReadsresponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, ResponseMessageResultType.NOT_OK, osgpException,
                    (PeriodicMeterReadsContainer) null);
        }
    }
}
