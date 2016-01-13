/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.MonitoringService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component("domainSmartMeteringActualMeterReadsResponseMessageProcessor")
public class ActualMeterReadsResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private MonitoringService monitoringService;

    protected ActualMeterReadsResponseMessageProcessor() {
        super(DeviceFunction.REQUEST_ACTUAL_METER_DATA);
    }

    @Override
    protected void handleMessage(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessage responseMessage,
            final OsgpException osgpException) {

        if (responseMessage.getDataObject() instanceof ActualMeterReads) {
            final ActualMeterReads actualMeterReadsDto = (ActualMeterReads) responseMessage.getDataObject();

            this.monitoringService.handleActualMeterReadsResponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, responseMessage.getResult(), osgpException, actualMeterReadsDto);
        } else if (responseMessage.getDataObject() instanceof MeterReadsGas) {
            final MeterReadsGas meterReadsGas = (MeterReadsGas) responseMessage.getDataObject();
            this.monitoringService.handleActualMeterReadsResponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, responseMessage.getResult(), osgpException, meterReadsGas);
        } else if (responseMessage.getDataObject() instanceof ActualMeterReadsQuery) {
            final OsgpException e;
            if (osgpException == null) {
                e = new TechnicalException(ComponentType.DOMAIN_SMART_METERING, "Error retrieving actual meter reads.",
                        null);
            } else {
                e = osgpException;
            }
            if (((ActualMeterReadsQuery) responseMessage.getDataObject()).isGas()) {
                this.monitoringService.handleActualMeterReadsResponse(deviceIdentification, organisationIdentification,
                        correlationUid, messageType, ResponseMessageResultType.NOT_OK, e, (MeterReadsGas) null);
            } else {
                this.monitoringService.handleActualMeterReadsResponse(deviceIdentification, organisationIdentification,
                        correlationUid, messageType, ResponseMessageResultType.NOT_OK, e, (ActualMeterReads) null);
            }
        } else if (osgpException == null) {
            this.monitoringService.handleActualMeterReadsResponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, ResponseMessageResultType.NOT_OK, new TechnicalException(
                            ComponentType.DOMAIN_SMART_METERING, "Error retrieving actual meter reads.", null),
                    (ActualMeterReads) null);
        } else {
            this.monitoringService.handleActualMeterReadsResponse(deviceIdentification, organisationIdentification,
                    correlationUid, messageType, ResponseMessageResultType.NOT_OK, osgpException,
                    (ActualMeterReads) null);
        }
    }
}
