/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import org.osgp.adapter.protocol.dlms.application.mapping.InstallationMapper;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmSwitches;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsInstallationService")
public class InstallationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private InstallationMapper installationMapper;

    /**
     * Constructor
     */
    public InstallationService() {
        // Parameterless constructor required for transactions...
    }

    // === ADD METER ===

    public void addMeter(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final SmartMeteringDevice smartMeteringDevice,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("addMeter called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            final DlmsDevice dlmsDevice = this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);

            this.dlmsDeviceRepository.save(dlmsDevice);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during addMeter", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    public void setAlarmNotifications(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final AlarmSwitches alarmSwitches,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("setAlarmNotifications called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {

            LOGGER.info("*******************************************************");
            LOGGER.info("*********** Set Alarm Notifications *******************");
            LOGGER.info("*******************************************************");
            LOGGER.info("*******************************************************");
            LOGGER.info("*********   Device:       {}   *******", deviceIdentification);
            LOGGER.info("*********   Enable:       {}   *******", alarmSwitches.getEnableAlarms());
            LOGGER.info("*********   Disable:       {}   *******", alarmSwitches.getDisableAlarms());
            LOGGER.info("************************************************************");
            LOGGER.info("************************************************************");
            LOGGER.info("************************************************************");

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during setAlarmNotifications", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender);
        }
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, null);

        responseMessageSender.send(responseMessage);
    }
}
