/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.publiclighting.application.services;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.validation.Identification;
import com.alliander.osgp.domain.core.valueobjects.HistoryTermType;
import com.alliander.osgp.domain.core.valueobjects.PowerUsageData;
import com.alliander.osgp.domain.core.valueobjects.PowerUsageHistoryResponse;
import com.alliander.osgp.domain.core.valueobjects.TimePeriod;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.PowerUsageHistoryResponseMessageDataContainerDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

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
            final String messageType) throws FunctionalException {

        LOGGER.info("GetPowerUsageHistory for organisationIdentification: {} for deviceIdentification: {}",
                organisationIdentification, deviceIdentification);

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.TimePeriodDto timePeriodDto = new com.alliander.osgp.dto.valueobjects.TimePeriodDto(
                timePeriod.getStartTime(), timePeriod.getEndTime());
        final com.alliander.osgp.dto.valueobjects.HistoryTermTypeDto historyTermTypeDto = this.domainCoreMapper
                .map(historyTermType, com.alliander.osgp.dto.valueobjects.HistoryTermTypeDto.class);
        final PowerUsageHistoryMessageDataContainerDto powerUsageHistoryMessageDataContainerDto = new PowerUsageHistoryMessageDataContainerDto(
                timePeriodDto, historyTermTypeDto);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, powerUsageHistoryMessageDataContainerDto), messageType, device.getIpAddress(),
                scheduleTime);
    }

    public void handleGetPowerUsageHistoryResponse(
            final PowerUsageHistoryResponseMessageDataContainerDto powerUsageHistoryResponseMessageDataContainerDto,
            final String organisationIdentification, final String deviceIdentification, final String correlationUid,
            final String messageType, final ResponseMessageResultType deviceResult, final OsgpException exception) {

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
                .withDataObject(powerUsageHistoryResponse).build();
        this.webServiceResponseMessageSender.send(responseMessage, this.getPowerUsageHistoryResponseTimeToLive);
    }
}
