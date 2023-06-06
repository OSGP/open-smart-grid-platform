// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.opensmartgridplatform.core.domain.model.domain.DomainRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.DomainInfo;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.DeviceRepository;
import org.opensmartgridplatform.domain.core.repositories.DomainInfoRepository;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeviceRegistrationMessageService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DeviceRegistrationMessageService.class);

  @Autowired private DeviceRepository deviceRepository;

  @Autowired private DomainInfoRepository domainInfoRepository;

  @Autowired private DomainRequestService domainRequestService;

  @Autowired private DeviceNetworkAddressCleanupService deviceNetworkAddressCleanupService;

  /**
   * Update device registration data (IP address, etc). Device is added (without an owner) when it
   * doesn't exist.
   *
   * @param deviceIdentification The device identification.
   * @param ipAddress The IP address of the device.
   * @param deviceType The type of the device, SSLD or PSLD.
   * @param hasSchedule In case the device has a schedule, this will be true.
   * @return Device with updated data
   * @throws UnknownHostException
   */
  @Transactional(value = "transactionManager")
  public Device updateRegistrationData(
      final String deviceIdentification,
      final String ipAddress,
      final String deviceType,
      final boolean hasSchedule)
      throws UnknownHostException {

    LOGGER.info(
        "updateRegistrationData called for device: {} ipAddress: {}, deviceType: {} hasSchedule: {}.",
        deviceIdentification,
        ipAddress,
        deviceType,
        hasSchedule);

    // Check for existing IP addresses
    this.deviceNetworkAddressCleanupService.clearDuplicateAddresses(
        deviceIdentification, ipAddress);

    Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);
    if (device == null) {
      // Device does not exist yet, create without an owner.
      device = this.createNewDevice(deviceIdentification, deviceType);
    }

    final InetAddress inetAddress = InetAddress.getByName(ipAddress);
    device.updateRegistrationData(inetAddress, deviceType);
    device.updateConnectionDetailsToSuccess();

    return this.deviceRepository.save(device);
  }

  private Device createNewDevice(final String deviceIdentification, final String deviceType) {
    Device device;
    if (Ssld.SSLD_TYPE.equalsIgnoreCase(deviceType)
        || Ssld.PSLD_TYPE.equalsIgnoreCase(deviceType)) {
      device = new Ssld(deviceIdentification);
    } else {
      device = new Device(deviceIdentification);
    }
    return device;
  }

  public void sendRequestMessageToDomainCore(
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final MessageType messageType) {

    final RequestMessage message =
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, null);

    final DomainInfo domainInfo =
        this.domainInfoRepository.findByDomainAndDomainVersion("CORE", "1.0");

    this.domainRequestService.send(message, messageType.name(), domainInfo);
  }
}
