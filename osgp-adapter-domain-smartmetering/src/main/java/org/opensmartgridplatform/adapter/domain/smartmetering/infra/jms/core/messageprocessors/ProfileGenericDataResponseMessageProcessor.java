/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.MonitoringService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.core.OsgpCoreResponseMessageProcessor;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ProfileGenericDataResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ProfileGenericDataResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringMonitoringService")
    private MonitoringService monitoringService;

    protected ProfileGenericDataResponseMessageProcessor() {
        super(MessageType.GET_PROFILE_GENERIC_DATA);
    }

    @Override
    protected boolean hasRegularResponseObject(final ResponseMessage responseMessage) {
        final Object dataObject = responseMessage.getDataObject();
        return dataObject instanceof ProfileGenericDataResponseDto;
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata,
            final ResponseMessage responseMessage, final OsgpException osgpException) throws FunctionalException {

        if (responseMessage.getDataObject() instanceof ProfileGenericDataResponseDto) {

            final ProfileGenericDataResponseDto profileGenericDataResponseDto = (ProfileGenericDataResponseDto) responseMessage
                    .getDataObject();

            this.monitoringService.handleProfileGenericDataResponse(deviceMessageMetadata, responseMessage.getResult(),
                    osgpException, profileGenericDataResponseDto);
        } else {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new OsgpException(ComponentType.DOMAIN_SMART_METERING,
                            "DataObject for response message should be of type ProfileGenericDataResponseDto"));
        }
    }
}
