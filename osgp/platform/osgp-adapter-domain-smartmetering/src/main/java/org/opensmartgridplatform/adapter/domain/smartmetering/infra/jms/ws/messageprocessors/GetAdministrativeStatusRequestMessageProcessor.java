/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.ws.messageprocessors;

import org.opensmartgridplatform.adapter.domain.smartmetering.application.services.ConfigurationService;
import org.opensmartgridplatform.adapter.domain.smartmetering.infra.jms.BaseRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageProcessorMap;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class GetAdministrativeStatusRequestMessageProcessor extends BaseRequestMessageProcessor {

    @Autowired
    @Qualifier("domainSmartMeteringConfigurationService")
    private ConfigurationService configurationService;

    @Autowired
    protected GetAdministrativeStatusRequestMessageProcessor(
            @Qualifier("domainSmartMeteringInboundWebServiceRequestsMessageProcessorMap") MessageProcessorMap messageProcessorMap) {
        super(messageProcessorMap, MessageType.GET_ADMINISTRATIVE_STATUS);
    }

    @Override
    protected void handleMessage(final DeviceMessageMetadata deviceMessageMetadata, final Object dataObject)
            throws FunctionalException {

        final AdministrativeStatusType administrativeStatusType = (AdministrativeStatusType) dataObject;

        this.configurationService.getAdministrativeStatus(deviceMessageMetadata, administrativeStatusType);
    }

}
