/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;

@Component
public class ActualMeterReadsRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private MonitoringService monitoringService;

    protected ActualMeterReadsRequestMessageProcessor() {
        super(DeviceRequestMessageType.REQUEST_ACTUAL_METER_DATA);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException {

        this.assertRequestObjectType(ActualMeterReadsQueryDto.class, requestObject);

        final ActualMeterReadsQueryDto actualMeterReadsRequest = (ActualMeterReadsQueryDto) requestObject;
        return this.monitoringService.requestActualMeterReads(conn, device, actualMeterReadsRequest);
    }
}
