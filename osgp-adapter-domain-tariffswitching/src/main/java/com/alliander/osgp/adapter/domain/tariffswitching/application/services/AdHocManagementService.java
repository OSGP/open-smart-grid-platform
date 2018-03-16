/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.tariffswitching.application.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.adapter.domain.shared.FilterLightAndTariffValuesHelper;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.RelayStatus;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatus;
import com.alliander.osgp.domain.core.valueobjects.DeviceStatusMapped;
import com.alliander.osgp.domain.core.valueobjects.DomainType;
import com.alliander.osgp.domain.core.valueobjects.TariffValue;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.NoDeviceResponseException;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.RequestMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "domainTariffSwitchingAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends AbstractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

    @Autowired
    private SsldRepository ssldRepository;

    /**
     * Constructor
     */
    public AdHocManagementService() {
        // Parameterless constructor required for transactions...
    }

    // === GET STATUS ===

    /**
     * Retrieve status of device and provide a mapped response (PublicLighting or
     * TariffSwitching)
     *
     * @param organisationIdentification
     *            identification of organisation
     * @param deviceIdentification
     *            identification of device
     * @param allowedDomainType
     *            domain type performing requesting the status
     *
     * @return status of device
     *
     * @throws FunctionalException
     */
    public void getStatus(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final DomainType allowedDomainType, final String messageType)
            throws FunctionalException {

        this.findOrganisation(organisationIdentification);
        final Device device = this.findActiveDevice(deviceIdentification);

        final com.alliander.osgp.dto.valueobjects.DomainTypeDto allowedDomainTypeDto = this.domainCoreMapper
                .map(allowedDomainType, com.alliander.osgp.dto.valueobjects.DomainTypeDto.class);

        this.osgpCoreRequestMessageSender.send(new RequestMessage(correlationUid, organisationIdentification,
                deviceIdentification, allowedDomainTypeDto), messageType, device.getIpAddress());
    }

    public void handleGetStatusResponse(final com.alliander.osgp.dto.valueobjects.DeviceStatusDto deviceStatusDto,
            final DomainType allowedDomainType, final String deviceIdentification,
            final String organisationIdentification, final String correlationUid, final String messageType,
            final ResponseMessageResultType deviceResult, final OsgpException exception) throws OsgpException {

        ResponseMessageResultType result = deviceResult;
        OsgpException osgpException = exception;
        DeviceStatusMapped deviceStatusMapped = null;

        if (deviceResult == ResponseMessageResultType.NOT_OK || exception != null) {
            LOGGER.error("Device Response not ok.", osgpException);
        } else {
            final DeviceStatus status = this.domainCoreMapper.map(deviceStatusDto, DeviceStatus.class);

            final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

            final List<DeviceOutputSetting> deviceOutputSettings = ssld.getOutputSettings();

            final Map<Integer, DeviceOutputSetting> dosMap = new HashMap<>();
            for (final DeviceOutputSetting dos : deviceOutputSettings) {
                dosMap.put(dos.getExternalId(), dos);
            }

            if (status != null) {
                deviceStatusMapped = new DeviceStatusMapped(
                        FilterLightAndTariffValuesHelper.filterTariffValues(status.getLightValues(), dosMap,
                                allowedDomainType),
                        FilterLightAndTariffValuesHelper.filterLightValues(status.getLightValues(), dosMap,
                                allowedDomainType),
                        status.getPreferredLinkType(), status.getActualLinkType(), status.getLightType(),
                        status.getEventNotificationsMask());

                this.updateDeviceRelayOverview(ssld, deviceStatusMapped);
            } else {
                result = ResponseMessageResultType.NOT_OK;
                osgpException = new TechnicalException(ComponentType.DOMAIN_TARIFF_SWITCHING,
                        "Device was not able to report status", new NoDeviceResponseException());
            }
        }

        ResponseMessage responseMessage = ResponseMessage.newResponseMessageBuilder()
                .withCorrelationUid(correlationUid).withOrganisationIdentification(organisationIdentification)
                .withDeviceIdentification(deviceIdentification).withResult(result).withOsgpException(osgpException)
                .withDataObject(deviceStatusMapped).build();
        this.webServiceResponseMessageSender.send(responseMessage);
    }

    /**
     * Updates the relay overview from a device based on the given device status.
     *
     * @param deviceIdentification
     *            The device to update.
     * @param deviceStatus
     *            The device status to update the relay overview with.
     * @throws TechnicalException
     *             Thrown when an invalid device identification is given.
     */
    private void updateDeviceRelayOverview(final Ssld device, final DeviceStatusMapped deviceStatusMapped) {
        final List<RelayStatus> relayStatuses = device.getRelayStatusses();

        for (final TariffValue tariffValue : deviceStatusMapped.getTariffValues()) {
            boolean updated = false;
            for (final RelayStatus relayStatus : relayStatuses) {
                if (relayStatus.getIndex() == tariffValue.getIndex()) {
                    relayStatus.setLastKnownState(tariffValue.isHigh());
                    relayStatus.setLastKnowSwitchingTime(DateTime.now().toDate());
                    updated = true;
                    break;
                }
            }
            if (!updated) {
                final RelayStatus newRelayStatus = new RelayStatus(device, tariffValue.getIndex(), tariffValue.isHigh(),
                        DateTime.now().toDate());
                relayStatuses.add(newRelayStatus);
            }
        }

        this.ssldRepository.save(device);
    }
}
