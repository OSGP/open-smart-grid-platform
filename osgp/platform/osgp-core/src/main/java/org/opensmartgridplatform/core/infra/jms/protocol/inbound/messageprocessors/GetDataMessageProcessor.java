/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.inbound.messageprocessors;

import java.io.Serializable;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.core.infra.jms.protocol.inbound.AbstractProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.core.repositories.DeviceAuthorizationRepository;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunctionGroup;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("oslpGetDataMessageProcessor")
public class GetDataMessageProcessor extends AbstractProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetDataMessageProcessor.class);
    private static final String DOMAIN = "DISTRIBUTION_AUTOMATION";
    private static final String DOMAIN_VERSION = "1.0";

    private final DeviceRepository deviceRepository;
    private final DeviceAuthorizationRepository deviceAuthorizationRepository;
    private final DomainRequestService domainRequestService;
    private final DomainInfoRepository domainInfoRepository;

    protected GetDataMessageProcessor(final DeviceRepository deviceRepository,
            final DeviceAuthorizationRepository deviceAuthorizationRepository,
            final DomainRequestService domainRequestService, final DomainInfoRepository domainInfoRepository) {
        super(MessageType.GET_DATA);
        this.deviceRepository = deviceRepository;
        this.deviceAuthorizationRepository = deviceAuthorizationRepository;
        this.domainRequestService = domainRequestService;
        this.domainInfoRepository = domainInfoRepository;
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        final MessageMetadata metadata = MessageMetadata.fromMessage(message);
        final String deviceIdentification = metadata.getDeviceIdentification();
        LOGGER.info("Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
                this.messageType, metadata.getOrganisationIdentification(), deviceIdentification);
        final RequestMessage requestMessage = (RequestMessage) message.getObject();
        this.processRequestMessage(deviceIdentification, requestMessage);
    }

    private void processRequestMessage(final String deviceIdentification, final RequestMessage requestMessage)
            throws JMSException {
        try {
            final Device device = this.getDevice(deviceIdentification);
            final String ownerOrganisationId = this.getOrganisationIdentificationOfOwner(device);
            this.sendPayload(ownerOrganisationId, requestMessage, requestMessage.getRequest());
            device.updateConnectionDetailsToSuccess();
            this.deviceRepository.save(device);
        } catch (final Exception e) {
            LOGGER.error("Exception during GET_DATA processing", e);
            throw new JMSException(String.format("%s occurred, reason: %s", e.getClass().getName(), e.getMessage()));
        }
    }

    private void sendPayload(final String ownerOrganisationId, final RequestMessage requestMessage,
            final Serializable payload) {
        LOGGER.info("Processing GET_DATA payload {}", payload);
        final RequestMessage requestForOwner = new RequestMessage(requestMessage.getCorrelationUid(),
                ownerOrganisationId, requestMessage.getDeviceIdentification(), requestMessage.getIpAddress(), payload);
        final DomainInfo domainInfo = this.domainInfoRepository.findByDomainAndDomainVersion(DOMAIN, DOMAIN_VERSION);
        this.domainRequestService.send(requestForOwner, DeviceFunction.GET_DATA.name(), domainInfo);
    }

    private Device getDevice(final String deviceIdentification) throws FunctionalException {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        if (device == null) {
            LOGGER.error("No known device for deviceIdentification {}", deviceIdentification);
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, ComponentType.OSGP_CORE,
                    new UnknownEntityException(Device.class, deviceIdentification));
        }
        return device;
    }

    private String getOrganisationIdentificationOfOwner(final Device device) throws OsgpException {
        final List<DeviceAuthorization> deviceAuthorizations =
                this.deviceAuthorizationRepository.findByDeviceAndFunctionGroup(
                device, DeviceFunctionGroup.OWNER);
        if (deviceAuthorizations == null || deviceAuthorizations.isEmpty()) {
            LOGGER.error("No owner authorization for deviceIdentification {} with alarm notification",
                    device.getDeviceIdentification());
            throw new FunctionalException(FunctionalExceptionType.UNAUTHORIZED, ComponentType.OSGP_CORE,
                    new UnknownEntityException(DeviceAuthorization.class, device.getDeviceIdentification()));
        }
        return deviceAuthorizations.get(0).getOrganisation().getOrganisationIdentification();
    }
}
