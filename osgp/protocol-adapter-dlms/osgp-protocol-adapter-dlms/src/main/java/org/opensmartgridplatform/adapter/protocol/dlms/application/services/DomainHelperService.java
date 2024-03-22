// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.util.Optional;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProvider;
import org.opensmartgridplatform.adapter.protocol.jasper.sessionproviders.SessionProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.stereotype.Service;

@Service(value = "dlmsDomainHelperService")
public class DomainHelperService {

  private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

  private final DlmsDeviceRepository dlmsDeviceRepository;

  private final SessionProviderService sessionProviderService;

  public DomainHelperService(
      final DlmsDeviceRepository dlmsDeviceRepository,
      final SessionProviderService sessionProviderService) {
    this.dlmsDeviceRepository = dlmsDeviceRepository;
    this.sessionProviderService = sessionProviderService;
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

    if (dlmsDevice.getIpAddress() != null) {
      return;
    }

    if (dlmsDevice.isIpAddressIsStatic()) {
      dlmsDevice.setIpAddress(messageMetadata.getNetworkAddress());
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

    if (sessionProvider == null) {
      throw new ProtocolAdapterException(
          "Session provider: Cannot find SessionProvider for device: "
              + dlmsDevice.getDeviceIdentification()
              + " and iccId: "
              + dlmsDevice.getIccId()
              + " and communicationProvider: "
              + dlmsDevice.getCommunicationProvider());
    }

    final Optional<String> deviceIpAddress =
        sessionProvider.getIpAddress(dlmsDevice.getDeviceIdentification(), dlmsDevice.getIccId());

    return deviceIpAddress.orElseThrow(
        () ->
            new FunctionalException(
                FunctionalExceptionType.SESSION_PROVIDER_ERROR,
                COMPONENT_TYPE,
                new ProtocolAdapterException(
                    "Session provider: No IP address was returned for device: "
                        + dlmsDevice.getDeviceIdentification()
                        + " and iccId: "
                        + dlmsDevice.getIccId()
                        + " and communicationProvider: "
                        + dlmsDevice.getCommunicationProvider())));
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
