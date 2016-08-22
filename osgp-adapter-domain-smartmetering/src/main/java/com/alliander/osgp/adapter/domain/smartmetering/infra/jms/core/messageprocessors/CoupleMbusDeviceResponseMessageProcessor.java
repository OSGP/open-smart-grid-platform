/**
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.InstallationService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component
public class CoupleMbusDeviceResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private InstallationService installationService;

    protected CoupleMbusDeviceResponseMessageProcessor() {
        super(DeviceFunction.COUPLE_MBUS_DEVICE);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.
     * OsgpCoreResponseMessageProcessor#hasRegularResponseObject(com.alliander.
     * osgp.shared.infra.jms.ResponseMessage)
     */
    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        // Only the result is used, no need to check the dataObject.
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.
     * OsgpCoreResponseMessageProcessor#handleMessage(com.alliander.osgp.shared.
     * infra.jms.DeviceMessageMetadata,
     * com.alliander.osgp.shared.infra.jms.ResponseMessage,
     * com.alliander.osgp.shared.exceptionhandling.OsgpException)
     */
    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {
        this.installationService.handleCoupleMbusDeviceResponse(deviceMessageMetadata, responseMessage.getResult(),
                osgpException);

    }

}
