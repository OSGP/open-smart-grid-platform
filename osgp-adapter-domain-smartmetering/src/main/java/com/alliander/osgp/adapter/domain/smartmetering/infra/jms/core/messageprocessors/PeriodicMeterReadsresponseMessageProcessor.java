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
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component
public class PeriodicMeterReadsresponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringMonitoringService")
    private MonitoringService monitoringService;

    protected PeriodicMeterReadsresponseMessageProcessor() {
        super(DeviceFunction.REQUEST_PERIODIC_METER_DATA);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        final Object dataObject = responseMessage.getDataObject();
        return dataObject instanceof PeriodicMeterReadsResponseDto
                || dataObject instanceof PeriodicMeterReadGasResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) {

        if (responseMessage.getDataObject() instanceof PeriodicMeterReadsResponseDto) {
            final PeriodicMeterReadsResponseDto periodicMeterReadsContainer = (PeriodicMeterReadsResponseDto) responseMessage
                    .getDataObject();

            this.monitoringService.handlePeriodicMeterReadsresponse(deviceMessageMetadata, responseMessage.getResult(),
                    osgpException, periodicMeterReadsContainer);
        } else if (responseMessage.getDataObject() instanceof PeriodicMeterReadGasResponseDto) {
            final PeriodicMeterReadGasResponseDto periodicMeterReadsContainerGas = (PeriodicMeterReadGasResponseDto) responseMessage
                    .getDataObject();

            this.monitoringService.handlePeriodicMeterReadsresponse(deviceMessageMetadata, responseMessage.getResult(),
                    osgpException, periodicMeterReadsContainerGas);
        }
    }
}
