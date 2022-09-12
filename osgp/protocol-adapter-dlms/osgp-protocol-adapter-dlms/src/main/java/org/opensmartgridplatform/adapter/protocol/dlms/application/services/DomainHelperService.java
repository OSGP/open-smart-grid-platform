/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.jasper.client.JasperWirelessSmsClient;
import org.opensmartgridplatform.adapter.protocol.jasper.exceptions.OsgpJasperException;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderService;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.exceptions.SessionProviderException;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service(value = "dlmsDomainHelperService")
public class DomainHelperService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DomainHelperService.class);

  private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

  private final DlmsDeviceRepository dlmsDeviceRepository;

  private final SessionProviderService sessionProviderService;

  private final JasperWirelessSmsClient jasperWirelessSmsClient;

  private final int jasperGetSessionRetries;

  private final int jasperGetSessionSleepBetweenRetries;

  public DomainHelperService(
      final DlmsDeviceRepository dlmsDeviceRepository,
      final SessionProviderService sessionProviderService,
      final JasperWirelessSmsClient jasperWirelessSmsClient,
      final int jasperGetSessionRetries,
      final int jasperGetSessionSleepBetweenRetries) {
    this.dlmsDeviceRepository = dlmsDeviceRepository;
    this.sessionProviderService = sessionProviderService;
    this.jasperWirelessSmsClient = jasperWirelessSmsClient;
    this.jasperGetSessionRetries = jasperGetSessionRetries;
    this.jasperGetSessionSleepBetweenRetries = jasperGetSessionSleepBetweenRetries;
  }

  /**
   * This method can be used to find an mBusDevice. For other devices, use {@link
   * #findDlmsDevice(MessageMetadata)} instead, as this will also set the IP address.
   */
  public DlmsDevice findDlmsDevice(final String deviceIdentification) throws FunctionalException {
    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByDeviceIdentification(deviceIdentification);
    if (dlmsDevice == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          COMPONENT_TYPE,
          new ProtocolAdapterException(
              "Unable to communicate with unknown device: " + deviceIdentification));
    }
    return dlmsDevice;
  }

  public DlmsDevice findDlmsDevice(final MessageMetadata messageMetadata) throws OsgpException {
    return this.findDlmsDevice(messageMetadata.getDeviceIdentification());
  }

  /**
   * Sets the transient ipAddress field of the given DLMS device to the IP address from the message
   * metadata if the device has a static IP address, or to an IP address obtained from the session
   * provider otherwise.
   *
   * @param dlmsDevice the device on which the IP address will be set
   * @param messageMetadata the message metadata containing the IP address to be used for devices
   *     with a static IP address
   * @throws OsgpException if such exception occurs getting the IP address from the session provider
   */
  public void setIpAddressFromMessageMetadataOrSessionProvider(
      final DlmsDevice dlmsDevice, final MessageMetadata messageMetadata) throws OsgpException {

    if (dlmsDevice.isIpAddressIsStatic()) {
      dlmsDevice.setIpAddress(messageMetadata.getIpAddress());
    } else {
      final String ipAddressFromSessionProvider =
          this.getDeviceIpAddressFromSessionProvider(dlmsDevice);
      dlmsDevice.setIpAddress(ipAddressFromSessionProvider);
    }
  }

  public String getDeviceIpAddressFromSessionProvider(final DlmsDevice dlmsDevice)
      throws OsgpException {

    final SessionProvider sessionProvider =
        this.sessionProviderService.getSessionProvider(dlmsDevice.getCommunicationProvider());
    String deviceIpAddress;
    try {
      deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getIccId());
      if (deviceIpAddress != null) {
        return deviceIpAddress;
      }

      // If the result is null then the meter is not in session (not
      // awake).
      // So wake up the meter and start polling for the session
      this.jasperWirelessSmsClient.sendWakeUpSMS(dlmsDevice.getIccId());
      deviceIpAddress = this.pollForSession(sessionProvider, dlmsDevice);

    } catch (final OsgpJasperException e) {
      throw new FunctionalException(
          FunctionalExceptionType.INVALID_ICCID, ComponentType.PROTOCOL_DLMS, e);
    }
    if ((deviceIpAddress == null) || "".equals(deviceIpAddress)) {
      throw new ProtocolAdapterException(
          "Session provider: "
              + dlmsDevice.getCommunicationProvider()
              + " did not return an IP address for device: "
              + dlmsDevice.getDeviceIdentification()
              + " and iccId: "
              + dlmsDevice.getIccId());
    }
    return deviceIpAddress;
  }

  private String pollForSession(final SessionProvider sessionProvider, final DlmsDevice dlmsDevice)
      throws OsgpException {

    String deviceIpAddress = null;
    try {
      for (int i = 0; i < this.jasperGetSessionRetries; i++) {
        Thread.sleep(this.jasperGetSessionSleepBetweenRetries);
        deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getIccId());
        if (deviceIpAddress != null) {
          return deviceIpAddress;
        }
      }
    } catch (final InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new ProtocolAdapterException(
          "Interrupted while sleeping before calling the sessionProvider.getIpAddress", e);
    } catch (final SessionProviderException e) {
      throw new ProtocolAdapterException("", e);
    }
    return deviceIpAddress;
  }

  public DlmsDevice findMbusDevice(
      final String mbusIdentificationNumber, final String mbusManufacturerIdentification)
      throws FunctionalException {
    final DlmsDevice dlmsDevice =
        this.dlmsDeviceRepository.findByMbusIdentificationNumberAndMbusManufacturerIdentification(
            mbusIdentificationNumber, mbusManufacturerIdentification);
    if (dlmsDevice == null) {
      throw new FunctionalException(
          FunctionalExceptionType.UNKNOWN_DEVICE,
          COMPONENT_TYPE,
          new ProtocolAdapterException(
              "Unable to find M-Bus device for M-Bus identification number: "
                  + mbusIdentificationNumber
                  + " and manufacturer ID: "
                  + mbusManufacturerIdentification));
    }
    return dlmsDevice;
  }
}
