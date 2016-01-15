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
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsInstallationService")
public class InstallationService extends DlmsApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InstallationService.class);

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private InstallationMapper installationMapper;

    // === ADD METER ===
    public void addMeter(final DlmsDeviceMessageMetadata messageMetadata,
            final SmartMeteringDevice smartMeteringDevice, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "addMeter");

        try {
            final DlmsDevice dlmsDevice = this.installationMapper.map(smartMeteringDevice, DlmsDevice.class);

            this.dlmsDeviceRepository.save(dlmsDevice);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during addMeter", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    smartMeteringDevice);
        }
    }

}
