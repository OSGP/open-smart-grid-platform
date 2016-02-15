/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.ConfigurationService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

/**
 * Class for processing smart metering set push setup sms response messages
 */
@Component("domainSmartMeteringSetPushSetupSmsResponseMessageProcessor")
public class SetPushSetupSmsResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    protected SetPushSetupSmsResponseMessageProcessor() {
        super(DeviceFunction.SET_PUSH_SETUP_SMS);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        // Only the result is used, no need to check the dataObject.
        return true;
    }

    @Override
    protected void handleMessage(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessage responseMessage,
            final OsgpException osgpException) {

        this.configurationService.handleSetPushSetupSmsResponse(deviceIdentification, organisationIdentification,
                correlationUid, messageType, responseMessage.getResult(), osgpException);
    }
}
