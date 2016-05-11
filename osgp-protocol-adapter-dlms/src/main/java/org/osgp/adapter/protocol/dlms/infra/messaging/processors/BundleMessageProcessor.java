/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.application.services.BundleService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessagesRequest;

/**
 * Class for processing find events request messages
 */
@Component
public class BundleMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private BundleService bundleService;

    public BundleMessageProcessor() {
        super(DeviceRequestMessageType.HANDLE_BUNDLED_ACTIONS);
    }

    @Override
    protected Serializable handleMessage(final ClientConnection conn, final DlmsDevice device,
            final Serializable requestObject) throws ProtocolAdapterException {

        if (!(requestObject instanceof BundleMessagesRequest)) {
            throw new ProtocolAdapterException(
                    "Expected request object of type BundleMessageDataContainer. In stead of "
                            + requestObject.getClass());
        }
        final BundleMessagesRequest bundleMessagesRequest = (BundleMessagesRequest) requestObject;

        return this.bundleService.callExecutors(conn, device, bundleMessagesRequest);
    }
}
