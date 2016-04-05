/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.ManagementService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

@Component("domainSmartmeteringDeactivateDeviceRequestMessageProcessor")
public class DeactivateDeviceRequestMessageProcessor extends WebServiceRequestMessageProcessor {

    @Autowired
    private ManagementService managementService;

    public DeactivateDeviceRequestMessageProcessor() {
        super(DeviceFunction.DEACTIVATE_DEVICE);
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
            throws FunctionalException {

        this.managementService.deactivateDevice(deviceMessageMetadata);
    }
}
