/**
 * Copyright 2015 Smart Society Services B.V.
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
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Component("domainSmartMeteringGetAdministrativeStateResponseMessageProcessor")
public class GetAdministrativeStateResponseMessageProcessor extends OsgpCoreResponseMessageProcessor {

    @Autowired
    private ConfigurationService configurationService;

    public GetAdministrativeStateResponseMessageProcessor() {
        super(DeviceFunction.GET_ADMINISTRATIVE_STATUS);
    }

    @Override
    protected void handleMessage(final String deviceIdentification, final String organisationIdentification,
            final String correlationUid, final String messageType, final ResponseMessage responseMessage,
            final OsgpException osgpException) {

        if (responseMessage.getDataObject() instanceof AdministrativeStatusType) {
            final AdministrativeStatusType administrativeStatusTypeDto = (AdministrativeStatusType) responseMessage
                    .getDataObject();
            this.configurationService.handleGetAdministrativeStatusResponse(deviceIdentification,
                    organisationIdentification, correlationUid, messageType, responseMessage.getResult(),
                    osgpException, administrativeStatusTypeDto);
        } else if (osgpException == null) {
            this.configurationService.handleGetAdministrativeStatusResponse(deviceIdentification,
                    organisationIdentification, correlationUid, messageType, ResponseMessageResultType.NOT_OK,
                    new TechnicalException(ComponentType.DOMAIN_SMART_METERING,
                            "Error retrieving administrative status.", null), (AdministrativeStatusType) null);
        } else {
            this.configurationService.handleGetAdministrativeStatusResponse(deviceIdentification,
                    organisationIdentification, correlationUid, messageType, ResponseMessageResultType.NOT_OK,
                    osgpException, (AdministrativeStatusType) null);
        }
    }
}
