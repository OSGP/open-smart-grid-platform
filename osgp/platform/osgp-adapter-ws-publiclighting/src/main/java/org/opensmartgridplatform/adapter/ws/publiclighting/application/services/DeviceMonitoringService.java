/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.services;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessage;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.publiclighting.infra.jms.PublicLightingResponseMessageFinder;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.services.CorrelationIdProviderService;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsageHistoryMessageDataContainer;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.DeviceMessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service(value = "wsPublicLightingDeviceMonitoringService")
@Transactional(value = "transactionManager")
@Validated
public class DeviceMonitoringService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMonitoringService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;

    @Autowired
    private PublicLightingRequestMessageSender publicLightingRequestMessageSender;

    @Autowired
    private PublicLightingResponseMessageFinder publicLightingResponseMessageFinder;

    public DeviceMonitoringService() {
        // Parameterless constructor required for transactions
    }

    // === GET POWER USAGE HISTORY ===

    public String enqueueGetPowerUsageHistoryRequest(final String organisationIdentification,
            final String deviceIdentification, final PowerUsageHistoryMessageDataContainer powerUsageContainer,
            final DateTime scheduledTime, final int messagePriority) throws FunctionalException {

        LOGGER.debug(
                "enqueueing GetPowerUsageHistoryRequest for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        final Organisation organisation = this.domainHelperService.findOrganisation(organisationIdentification);
        final Device device = this.domainHelperService.findActiveDevice(deviceIdentification);

        this.domainHelperService.isAllowed(organisation, device, DeviceFunction.GET_POWER_USAGE_HISTORY);

        final String correlationUid = this.correlationIdProviderService.getCorrelationId(organisationIdentification,
                deviceIdentification);

        final DeviceMessageMetadata deviceMessageMetadata = new DeviceMessageMetadata(deviceIdentification,
                organisationIdentification, correlationUid, MessageType.GET_POWER_USAGE_HISTORY.name(), messagePriority,
                scheduledTime == null ? null : scheduledTime.getMillis());

        final PublicLightingRequestMessage message = new PublicLightingRequestMessage.Builder()
                .deviceMessageMetadata(deviceMessageMetadata).request(powerUsageContainer).build();

        this.publicLightingRequestMessageSender.send(message);

        return correlationUid;
    }

    public ResponseMessage dequeueGetPowerUsageHistoryResponse(final String organisationIdentification,
            final String correlationUid) throws OsgpException {

        LOGGER.debug(
                "dequeueing GetPowerUsageHistoryResponse for organisationIdentification: {} for correlationUid: {}",
                organisationIdentification, correlationUid);

        return this.publicLightingResponseMessageFinder.findMessage(correlationUid);
    }
}
