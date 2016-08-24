/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.BundleService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.OsgpResultTypeDto;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component
public class BundleResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringBundleService")
    private BundleService bundleService;

    public BundleResponseMessageProcessor() {
        super(DeviceFunction.HANDLE_BUNDLED_ACTIONS);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return responseMessage.getDataObject() instanceof BundleMessagesRequestDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {

        final BundleMessagesRequestDto bundleMessagesResponseDto = (BundleMessagesRequestDto) responseMessage
                .getDataObject();

        this.bundleService.handleBundleResponse(deviceMessageMetadata, responseMessage.getResult(), osgpException,
                bundleMessagesResponseDto);
    }

    @Override
    protected void handleError(final Exception e, final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage) throws FunctionalException {

        final OsgpException osgpException = this.ensureOsgpException(e);

        final BundleMessagesRequestDto bundleMessagesResponseDto = (BundleMessagesRequestDto) responseMessage
                .getDataObject();

        final List<ActionDto> actionList = bundleMessagesResponseDto.getActionList();
        for (final ActionDto action : actionList) {
            if (action.getResponse() == null) {
                action.setResponse(
                        new ActionResponseDto(OsgpResultTypeDto.NOT_OK, e, "Unable to handle request"));
            }
        }

        this.bundleService.handleBundleResponse(deviceMessageMetadata, responseMessage.getResult(), osgpException,
                bundleMessagesResponseDto);
    }
}
