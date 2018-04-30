/**
l * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.domain.smartmetering.application.services.AdhocService;
import com.alliander.osgp.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsResponseDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;

@Component
public class ScanMbusChannelsResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringAdhocService")
    private AdhocService adhocService;

    public ScanMbusChannelsResponseMessageProcessor() {
        super(DeviceFunction.SCAN_MBUS_CHANNELS);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        return responseMessage.getDataObject() instanceof ScanMbusChannelsResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {

        if (responseMessage.getDataObject() instanceof ScanMbusChannelsResponseDto) {
            final ScanMbusChannelsResponseDto mbusAttributesList = (ScanMbusChannelsResponseDto) responseMessage
                    .getDataObject();

            this.adhocService.handleScanMbusChannelsResponse(deviceMessageMetadata, responseMessage.getResult(),
                    osgpException, mbusAttributesList);
        } else {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR, ComponentType.DOMAIN_SMART_METERING,
                    new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "DataObject for response message should be of type ScanMbusChannelsResponseDto"));
        }

    }
}
