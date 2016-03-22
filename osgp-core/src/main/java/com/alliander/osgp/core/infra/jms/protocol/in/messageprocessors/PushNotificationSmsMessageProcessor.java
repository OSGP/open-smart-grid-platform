/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.core.infra.jms.protocol.in.messageprocessors;

import java.net.InetAddress;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.application.services.EventNotificationMessageService;
import com.alliander.osgp.core.domain.model.domain.DomainRequestService;
import com.alliander.osgp.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.exceptions.UnknownEntityException;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.DomainInfoRepository;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunction;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushNotificationSmsDto;
import com.alliander.osgp.shared.infra.jms.Constants;
import com.alliander.osgp.shared.infra.jms.RequestMessage;

@Component("dlmsPushNotificationSmsMessageProcessor")
@Transactional(value = "transactionManager")
public class PushNotificationSmsMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(PushNotificationSmsMessageProcessor.class);

    @Autowired
    private EventNotificationMessageService eventNotificationMessageService;

    @Autowired
    private DomainRequestService domainRequestService;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    protected PushNotificationSmsMessageProcessor() {
        super(DeviceFunction.PUSH_NOTIFICATION_SMS);
    }

    @Override
    public void processMessage(final ObjectMessage message) throws JMSException {
        final String messageType = message.getJMSType();
        final String organisationIdentification = message.getStringProperty(Constants.ORGANISATION_IDENTIFICATION);
        final String deviceIdentification = message.getStringProperty(Constants.DEVICE_IDENTIFICATION);

        LOGGER.info("Received message of messageType: {} organisationIdentification: {} deviceIdentification: {}",
                messageType, organisationIdentification, deviceIdentification);

        final RequestMessage requestMessage = (RequestMessage) message.getObject();
        final Object dataObject = requestMessage.getRequest();

        try {
            final PushNotificationSmsDto pushNotificationSms = (PushNotificationSmsDto) dataObject;

            this.storeSmsAsEvent(pushNotificationSms);

            if (pushNotificationSms.getIpAddress() != null && !"".equals(pushNotificationSms.getIpAddress())) {

                LOGGER.info("Updating device {} IP address from {} to {}", deviceIdentification,
                        requestMessage.getIpAddress(), pushNotificationSms.getIpAddress());

                // Convert the IP address from String to InetAddress.
                final InetAddress address = InetAddress.getByName(pushNotificationSms.getIpAddress());

                // Lookup device
                final Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
                if (device != null) {
                    device.updateRegistrationData(address, device.getDeviceType());
                    this.deviceRepository.save(device);
                } else {
                    LOGGER.warn(
                            "Device with ID = {} not found. Discard Sms notification request from ip address = {} of device",
                            deviceIdentification, address);
                }
            } else {
                LOGGER.warn("Sms notification request for device = {} has no new IP address. Discard request.",
                        deviceIdentification);
            }

        } catch (final Exception e) {
            LOGGER.error("Exception", e);
            throw new JMSException(e.getMessage());
        }
    }

    private void storeSmsAsEvent(final PushNotificationSmsDto pushNotificationSms) {
        try {
            this.eventNotificationMessageService.handleEvent(pushNotificationSms.getDeviceIdentification(),
                    com.alliander.osgp.domain.core.valueobjects.EventType.SMS_NOTIFICATION, pushNotificationSms
                    .getIpAddress().toString(), 0);
        } catch (final UnknownEntityException uee) {
            LOGGER.warn("Unable to store event for Push Notification Sms from unknown device: {}", pushNotificationSms,
                    uee);
        } catch (final Exception e) {
            LOGGER.error("Error storing event for Push Notification Sms: {}", pushNotificationSms, e);
        }
    }
}
