/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.services;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainDistributionAutomationCommunicationRecoveryService")
@Transactional(value = "transactionManager")
public class CommunicationRecoveryService extends BaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationRecoveryService.class);

    @Autowired
    private CorrelationIdProviderService correlationIdProviderService;
    @Autowired
    @Qualifier("domainDistributionAutomationDeviceManagementService")
    private DeviceManagementService deviceManagementService;

    /**
     * Send a signal that the connection with the device has been lost. This is
     * done by putting a GetHealthStatus on the queue with an alarm value. When
     * this response is received by the webservice adapter, it can send a
     * notification to the client.
     *
     * @param rtu
     */
    public void signalConnectionLost(final RtuDevice rtu) {
        LOGGER.info("Sending connection lost signal for device {}.", rtu.getDeviceIdentification());

        final GetHealthStatusResponseDto getHealthStatusResponseDto = new GetHealthStatusResponseDto("NOTRESPONDING");

        final String correlationUid = this.createCorrelationUid(rtu);
        final String organisationIdentification = rtu.getOwner().getOrganisationIdentification();
        final String deviceIdentification = rtu.getDeviceIdentification();

        this.deviceManagementService
                .handleHealthStatusResponse(getHealthStatusResponseDto, deviceIdentification, organisationIdentification, correlationUid,
                        DeviceFunction.GET_DATA.toString(), ResponseMessageResultType.OK, null);
    }

    public void restoreCommunication(final RtuDevice rtu) {
        LOGGER.info("Restoring communication for device {}.", rtu.getDeviceIdentification());

        if (rtu.getOwner() == null) {
            LOGGER.warn("Device {} has no owner. Skipping communication recovery.", rtu.getDeviceIdentification());
            return;
        }

        final RequestMessage message = this.createMessage(rtu);
        this.osgpCoreRequestMessageSender.send(message, DeviceFunction.GET_DATA.toString(), rtu.getIpAddress());
    }

    private RequestMessage createMessage(final RtuDevice rtu) {
        LOGGER.debug("Creating message for device {}.", rtu.getDeviceIdentification());

        final String correlationUid = this.createCorrelationUid(rtu);
        final String organisationIdentification = rtu.getOwner().getOrganisationIdentification();
        final String deviceIdentification = rtu.getDeviceIdentification();
        final GetHealthStatusRequestDto request = this.createHalthStatusRequest(rtu);

        return new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, request);
    }

    private String createCorrelationUid(final RtuDevice rtu) {
        LOGGER.debug("Creating correlation uid for device {}, with owner {}", rtu.getDeviceIdentification(),
                rtu.getOwner().getOrganisationIdentification());

        final String correlationUid = this.correlationIdProviderService
                .getCorrelationId(rtu.getOwner().getOrganisationIdentification(), rtu.getDeviceIdentification());

        LOGGER.debug("Correlation uid {} created.", correlationUid);

        return correlationUid;
    }

    private GetHealthStatusRequestDto createHalthStatusRequest(final RtuDevice rtu) {
        LOGGER.debug("Creating Health Status request for rtu {}.", rtu.getDeviceIdentification());
        return new GetHealthStatusRequestDto();
    }
}
