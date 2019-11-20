/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.core.application.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.entities.SsldPendingFirmwareUpdate;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderTimestampService;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceRegistrationMessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRegistrationMessageService.class);

    private static final String LOCAL_HOST = "127.0.0.1";

    private static final String SSLD_PENDING_FIRMWARE_UPDATE = "SSLD_PENDING_FIRMWARE_UPDATE";

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DomainInfoRepository domainInfoRepository;

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private DomainRequestService domainRequestService;

    @Autowired
    private CorrelationIdProviderTimestampService correlationIdProviderTimestampService;

    /**
     * Update device registration data (IP address, etc). Device is added
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
    @Transactional(value = "transactionManager")
    public Device updateRegistrationData(final String deviceIdentification, final String ipAddress,
            final String deviceType, final boolean hasSchedule) throws UnknownHostException {

        LOGGER.info("updateRegistrationData called for device: {} ipAddress: {}, deviceType: {} hasSchedule: {}.",
                deviceIdentification, ipAddress, deviceType, hasSchedule);

        // Convert the IP address from String to InetAddress.
        final InetAddress address = LOCAL_HOST.equals(ipAddress) ? InetAddress.getLoopbackAddress()
                : InetAddress.getByName(ipAddress);

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
        device.updateConnectionDetailsToSuccess();

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

    public void checkSsldPendingFirmwareUpdate(final String deviceIdentification) {

        final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
        if (ssld == null) {
            LOGGER.warn("No SSLD found for device identification: {}.", deviceIdentification);
            return;
        }

        final SsldPendingFirmwareUpdate ssldPendingFirmwareUpdate = ssld.getSsldPendingFirmwareUpdate();
        if (ssldPendingFirmwareUpdate == null) {
            LOGGER.info("No SSLD pending firmware update found for device identification: {}.", deviceIdentification);
            return;
        }

        if (ssldPendingFirmwareUpdate.hasPendingFirmwareUpdate()) {
            final String organisationIdentification = ssldPendingFirmwareUpdate.getOrganisationIdentification();

            final String correlationUid = this.correlationIdProviderTimestampService
                    .getCorrelationId(organisationIdentification, deviceIdentification);

            final RequestMessage message = new RequestMessage(correlationUid, organisationIdentification,
                    deviceIdentification, ssldPendingFirmwareUpdate);

            final DomainInfo domainInfo = this.domainInfoRepository.findByDomainAndDomainVersion(
                    ssldPendingFirmwareUpdate.getDomain(), ssldPendingFirmwareUpdate.getDomainVersion());

            this.domainRequestService.send(message, SSLD_PENDING_FIRMWARE_UPDATE, domainInfo);
        }
    }
}
