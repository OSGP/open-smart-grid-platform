/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.infra.messaging.processors;

import java.io.Serializable;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.application.jasper.sessionproviders.exceptions.SessionProviderException;
import org.osgp.adapter.protocol.dlms.application.services.ManagementService;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageProcessor;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceRequestMessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.shared.exceptionhandling.OsgpException;

/**
 * Class for processing find events request messages
 */
@Component("dlmsBundleRequestMessageProcessor")
public class BundleRequestMessageProcessor extends DeviceRequestMessageProcessor {

    @Autowired
    private ManagementService managementService;

    // @Autowired
    // @Qualifier("protocolDlmsDeviceRequestMessageProcessorMap")
    // protected MessageProcessorMap dlmsRequestMessageProcessorMap;

    public BundleRequestMessageProcessor() {
        super(DeviceRequestMessageType.FIND_EVENTS);
    }

    @Override
    protected Serializable handleMessage(final ClientConnection conn, final DlmsDevice device,
            final Serializable requestObject) throws OsgpException, ProtocolAdapterException, SessionProviderException {

        // final MessageProcessor processor =
        // this.dlmsRequestMessageProcessorMap.getMessageProcessor(requestObject);
        //
        // processor.processMessage(requestObject);

        // if (requestObject instanceof FindEventsQueryMessageDataContainer) {
        // final FindEventsQueryMessageDataContainer new_name =
        // (FindEventsQueryMessageDataContainer) requestObject;
        //
        // }
        //
        // for(Request c: requestObject){
        // c.getType -->
        // }
        //
        // this.managementService.findEvents(messageMetadata,
        // (FindEventsQueryMessageDataContainer) requestObject);
        // this.managementService.periodicMeter(messageMetadata,
        // (FindEventsQueryMessageDataContainer) requestObject);
        // this.managementService.periodicMeter(messageMetadata,
        // (FindEventsQueryMessageDataContainer) requestObject);
        // this.managementService.periodicMeter(messageMetadata,
        // (FindEventsQueryMessageDataContainer) requestObject);
        // this.managementService.periodicMeter(messageMetadata,
        // (FindEventsQueryMessageDataContainer) requestObject);
        // this.managementService.periodicMeter(messageMetadata,
        // (FindEventsQueryMessageDataContainer) requestObject);
        //
        return null;
    }
}

// DeviceRequestMessageType.FIND_EVENTS should be mapped to
// this.managementService.findEvents(messageMetadata,
// (FindEventsQueryMessageDataContainer) this.requestObject);

