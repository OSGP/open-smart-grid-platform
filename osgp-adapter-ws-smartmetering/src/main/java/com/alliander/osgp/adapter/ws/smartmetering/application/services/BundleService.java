/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.smartmetering.domain.entities.MeterResponseData;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessage;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageSender;
import com.alliander.osgp.adapter.ws.smartmetering.infra.jms.SmartMeteringRequestMessageType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.services.CorrelationIdProviderService;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import com.alliander.osgp.shared.exceptionhandling.CorrelationUidException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.infra.jms.DeviceMessageMetadata;

@Service(value = "wsSmartMeteringBundleService")
@Transactional(value = "transactionManager")
@Validated
public class BundleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private MeterResponseDataService meterResponseDataService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private SmartMeteringRequestMessageSender smartMeteringRequestMessageSender;

    public BundleService() {
        // Parameterless constructor required for transactions
    }

    public MeterResponseData dequeueBundleResponse(final String correlationUid) throws CorrelationUidException {
        return this.meterResponseDataService.dequeue(correlationUid, BundleMessagesResponse.class);
    }

    public String enqueueBundleRequest(final String organisationIdentification, final String deviceIdentification,
            final List<ActionRequest> actionList, final int messagePriority) throws FunctionalException {

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.HANDLE_BUNDLED_ACTIONS);

        LOGGER.info("Bundle request called with organisation {}", organisationIdentification);

        for (final ActionRequest action : actionList) {

            action.validate();
        }

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid,
                SmartMeteringRequestMessageType.HANDLE_BUNDLED_ACTIONS.toString(), messagePriority);

        // @formatter:off
        final SmartMeteringRequestMessage message = new SmartMeteringRequestMessage.Builder()
        .deviceMessageMetadata(deviceMessageMetadata)
        .request(new BundleMessageRequest(actionList))
        .build();
        // @formatter:on

        this.smartMeteringRequestMessageSender.send(message);

        return correlationUid;
    }

}
