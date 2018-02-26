/**
 * Copyright 2018 Smart Society Services B.V.
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
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseDto;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component
public class GetMbusEncryptionKeyStatusByChannelResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    public GetMbusEncryptionKeyStatusByChannelResponseMessageProcessor() {
        super(DeviceFunction.GET_MBUS_ENCRYPTION_KEY_STATUS_BY_CHANNEL);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return responseMessage.getDataObject() instanceof GetMbusEncryptionKeyStatusByChannelResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) {

        final GetMbusEncryptionKeyStatusByChannelResponseDto getMbusEncryptionKeyStatusByChannelResponseDto = (GetMbusEncryptionKeyStatusByChannelResponseDto) responseMessage
                .getDataObject();
        this.configurationService.handleGetMbusEncryptionKeyStatusByChannelResponse(deviceMessageMetadata,
                responseMessage.getResult(), osgpException, getMbusEncryptionKeyStatusByChannelResponseDto);
    }
}
