/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.publiclighting.application.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.validation.Identification;
import org.opensmartgridplatform.domain.core.valueobjects.HistoryTermType;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsageData;
import org.opensmartgridplatform.domain.core.valueobjects.PowerUsageHistoryResponse;
import org.opensmartgridplatform.domain.core.valueobjects.TimePeriod;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import org.opensmartgridplatform.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainPublicLightingDeviceMonitoringService")
@Transactional(value = "transactionManager")
public class DeviceMonitoringService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceMonitoringService.class);

    @Autowired
    private Long getPowerUsageHistoryResponseTimeToLive;

    /**
     * Constructor
     */
    public DeviceMonitoringService() {
        // Parameterless constructor required for transactions...
    }

    // === GET POWER USAGE HISTORY ===

    public void getPowerUsageHistory(@Identification final String organisationIdentification,
            @Identification final String deviceIdentification, final String correlationUid,
            @Valid final TimePeriod timePeriod, @NotNull final HistoryTermType historyTermType, final Long scheduleTime,
            final String messageType, final int messagePriority) throws FunctionalException {

        LOGGER.info("GetPowerUsageHistory for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final org.opensmartgridplatform.dto.valueobjects.TimePeriodDto timePeriodDto = new org.opensmartgridplatform.dto.valueobjects.TimePeriodDto(
                timePeriod.getStartTime(), timePeriod.getEndTime());
        final org.opensmartgridplatform.dto.valueobjects.HistoryTermTypeDto historyTermTypeDto = this.domainCoreMapper
                .map(historyTermType, org.opensmartgridplatform.dto.valueobjects.HistoryTermTypeDto.class);
        final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryMessageDataContainerDto = new PowerUsageHistoryMessageDataContainerDto(
                timePeriodDto, historyTermTypeDto);

        this.osgpCoreRequestMessageSender.send(
                new RequestMessage(correlationUid, organisationIdentification, deviceIdentification,
                        powerUsageHistoryMessageDataContainerDto),
                messageType, messagePriority, device.getIpAddress(), scheduleTime);
    }

    public void handleGetPowerUsageHistoryResponse(
            final PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainerDto,
            final String organisationIdentification, final String deviceIdentification, final String correlationUid,
            final String messageType, final int messagePriority, final ResponseMessageResultType deviceResult,
            final OsgpException exception) {

        LOGGER.info("handleResponse called for device: {} for organisation: {} for messageType: {}",
                deviceIdentification, organisationIdentification, messageType);

        ResponseMessageResultType result = ResponseMessageResultType.OK;
        OsgpException osgpException = exception;
        PowerUsageHistoryResponse powerUsageHistoryResponse = null;

        try {
            if (deviceResult == ResponseMessageResultType.NOT_OK || osgpException != null) {
                LOGGER.error("Device Response not ok.", osgpException);
                throw osgpException;
            }

            powerUsageHistoryResponse = new PowerUsageHistoryResponse(this.domainCoreMapper.mapAsList(
                    powerUsageHistoryResponseMessageDataContainerDto.getPowerUsageData(), PowerUsageData.class));

        } catch (final Exception e) {
            LOGGER.error("Unexpected Exception", e);
            result = ResponseMessageResultType.NOT_OK;
            osgpException = new TechnicalException(ComponentType.UNKNOWN,
                    "Exception occurred while getting device power usage history", e);
        }

        final ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(osgpException)
                .withDataObject(powerUsageHistoryResponse).withMessagePriority(messagePriority).build();
        this.webServiceResponseMessageSender.send(responseMessage, this.getPowerUsageHistoryResponseTimeToLive);
    }
}
