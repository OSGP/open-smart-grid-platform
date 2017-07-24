/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.InstallationService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.ws.WebServiceRequestMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

/**
 * this class holds functionality to implement the message handling of a request
 * to couple a device and a m-bus device
 */
@Component
public class CoupleMbusDeviceRequestMessageProcessor extends WebServiceRequestMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringInstallationService")
    private InstallationService installationService;

    protected CoupleMbusDeviceRequestMessageProcessor() {
        super(DeviceFunction.COUPLE_MBUS_DEVICE);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.adapter.domain.smartmetering.infra.jms.
     * AbstractRequestMessageProcessor#handleMessage(com.alliander.osgp.shared.
     * infra.jms.DeviceMessageMetadata, java.lang.Object)
     */
    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
            throws FunctionalException {
        final CoupleMbusDeviceRequestData requestData = (CoupleMbusDeviceRequestData) dataObject;
        this.installationService.coupleMbusDevice(deviceMessageMetadata, requestData);
    }

}
