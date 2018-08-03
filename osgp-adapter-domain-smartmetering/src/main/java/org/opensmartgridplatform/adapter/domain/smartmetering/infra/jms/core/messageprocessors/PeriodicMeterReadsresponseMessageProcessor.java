/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadGasResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;

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
