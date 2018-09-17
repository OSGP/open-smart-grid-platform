/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.infra.jms.protocol.in.messageprocessors;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.opensmartgridplatform.core.infra.jms.protocol.in.ProtocolRequestMessageProcessor;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.dto.valueobjects.DeviceRegistrationDataDto;
import org.opensmartgridplatform.shared.infra.jms.Constants;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component("oslpRegisterDeviceMessageProcessor")
@Transactional(value = "transactionManager")
public class RegisterDeviceMessageProcessor extends ProtocolRequestMessageProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(RegisterDeviceMessageProcessor.class);

    @Autowired
    private DeviceRepository deviceRepository;

    private static final String LOCAL_HOST = "127.0.0.1";

    protected RegisterDeviceMessageProcessor() {
        super(MessageType.REGISTER_DEVICE);
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
            final DeviceRegistrationDataDto deviceRegistrationData = (DeviceRegistrationDataDto) dataObject;

            this.updateRegistrationData(deviceIdentification, deviceRegistrationData.getIpAddress(),
                    deviceRegistrationData.getDeviceType(), deviceRegistrationData.isHasSchedule());
        } catch (final UnknownHostException e) {
            LOGGER.error("Exception", e);
            throw new JMSException(e.getMessage());
        }
    }

    // === REGISTER DEVICE ===

    /**
     * Update device registration data (ipaddress, etc). Device is added
     * (without an owner) when not exist yet.
     *
     * @param deviceIdentification
     *            The device identification.
     * @param ipAddress
     *            The IP address of the device.
     * @param deviceType
     *            The type of the device, SSLD or PSLD.
     * @param hasSchedule
     *            In case the device has a schedule, this will be true.
     *
     * @return Device with updated data
     *
     * @throws UnknownHostException
     */
    private Device updateRegistrationData(final String deviceIdentification, final String ipAddress,
            final String deviceType, final boolean hasSchedule) throws UnknownHostException {

        LOGGER.info("updateRegistrationData called for device: {} ipAddress: {}, deviceType: {} hasSchedule: {}.",
                deviceIdentification, ipAddress, deviceType, hasSchedule);

        // Convert the IP address from String to InetAddress.
        final InetAddress address = LOCAL_HOST.equals(ipAddress) ? InetAddress.getLoopbackAddress() : InetAddress
                .getByName(ipAddress);

        // Lookup device
        Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        // Check for existing IP addresses
        this.clearDuplicateAddresses(deviceIdentification, address);

        if (device == null) {
            // Device does not exist yet, create without an owner.
            device = this.createNewDevice(deviceIdentification, deviceType);
        }

        // Device already exists, update registration data
        device.updateRegistrationData(address, deviceType);
        return this.deviceRepository.save(device);
    }

    private Device createNewDevice(final String deviceIdentification, final String deviceType) {
        Device device;
        if (Ssld.SSLD_TYPE.equalsIgnoreCase(deviceType) || Ssld.PSLD_TYPE.equalsIgnoreCase(deviceType)) {
            device = new Ssld(deviceIdentification);
        } else {
            device = new Device(deviceIdentification);
        }
        return device;
    }

    private void clearDuplicateAddresses(final String deviceIdentification, final InetAddress address) {
        final List<Device> devices = this.deviceRepository.findByNetworkAddress(address);

        for (final Device device : devices) {
            if (!LOCAL_HOST.equals(device.getIpAddress())
                    && !device.getDeviceIdentification().equals(deviceIdentification)) {
                device.clearNetworkAddress();
                this.deviceRepository.save(device);
            }
        }
    }
}
