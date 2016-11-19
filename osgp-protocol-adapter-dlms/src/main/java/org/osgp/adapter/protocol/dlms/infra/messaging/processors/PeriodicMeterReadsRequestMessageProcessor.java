/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.application.services.MonitoringService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.osgp.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing Periodic Meter Request messages
 */
@Component
public class PeriodicMeterReadsRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private MonitoringService monitoringService;

    public PeriodicMeterReadsRequestMessageProcessor() {
        super(DeviceRequestMessageType.REQUEST_PERIODIC_METER_DATA);
    }

    @Override
    protected Serializable handleMessage(final DlmsConnectionHolder conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException, SessionProviderException {

        this.assertRequestObjectType(PeriodicMeterReadsRequestDto.class, requestObject);

        final PeriodicMeterReadsRequestDto periodicMeterReadsQuery = (PeriodicMeterReadsRequestDto) requestObject;

        return this.monitoringService.requestPeriodicMeterReads(conn, device, periodicMeterReadsQuery);
    }
}
