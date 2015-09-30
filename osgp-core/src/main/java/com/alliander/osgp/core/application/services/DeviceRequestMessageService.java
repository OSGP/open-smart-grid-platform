/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.application.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.domain.model.domain.DomainResponseService;
import com.alliander.osgp.core.domain.model.protocol.ProtocolRequestService;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.entities.SmartMeteringDevice;
import com.alliander.osgp.domain.core.repositories.SmartMeteringDeviceRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.infra.jms.ProtocolRequestMessage;

@Service
@Transactional
public class DeviceRequestMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageService.class);

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DomainResponseService domainResponseMessageSender;

    @Autowired
    private ProtocolRequestService protocolRequestService;

    @Autowired
    private SmartMeteringDeviceRepository smartMeteringDeviceRepository;

    public void processMessage(final ProtocolRequestMessage message) throws FunctionalException {

        try {
            ProtocolInfo protocolInfo = null;

            // TODO workaround for SSLD specific authorization
            if (message.getDomain().equals("SMART_METERING")) {

                final SmartMeteringDevice smartMeteringDevice = this.smartMeteringDeviceRepository
                        .findByDeviceIdentification(message.getDeviceIdentification());
                protocolInfo = smartMeteringDevice.getProtocolInfo();
            } else {

                final Organisation organisation = this.domainHelperService.findOrganisation(message
                        .getOrganisationIdentification());

                final Device device = this.domainHelperService.findDevice(message.getDeviceIdentification());
                this.domainHelperService.isAllowed(organisation, device,
                        Enum.valueOf(DeviceFunction.class, message.getMessageType()));

                protocolInfo = device.getProtocolInfo();

                if (protocolInfo == null) {
                    final String msg = "Protocol unknown for device [" + device.getDeviceIdentification() + "]";
                    LOGGER.error(msg);
                    throw new FunctionalException(FunctionalExceptionType.PROTOCOL_UNKNOWN_FOR_DEVICE,
                            ComponentType.OSGP_CORE);
                } else {
                    LOGGER.info("Device is using protocol [{}] with version [{}]", protocolInfo.getProtocol(),
                            protocolInfo.getProtocolVersion());
                }
            }

            this.protocolRequestService.send(message, protocolInfo);

        } catch (final FunctionalException e) {
            this.domainResponseMessageSender.send(message, e);
            throw e;
        }
    }
}
