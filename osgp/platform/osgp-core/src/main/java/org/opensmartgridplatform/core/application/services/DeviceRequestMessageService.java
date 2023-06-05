// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.services;

import org.opensmartgridplatform.core.domain.model.domain.DomainResponseService;
import org.opensmartgridplatform.core.domain.model.protocol.ProtocolRequestService;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.ProtocolInfo;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.infra.jms.ProtocolRequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceRequestMessageService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceRequestMessageService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private DomainResponseService domainResponseMessageSender;

  @Autowired private ProtocolRequestService protocolRequestService;

  public void processMessage(final ProtocolRequestMessage message) throws FunctionalException {

    try {

      final Device device = this.domainHelperService.findDevice(message.getDeviceIdentification());
      final ProtocolInfo protocolInfo;
      if (device.getGatewayDevice() == null) {
        protocolInfo = device.getProtocolInfo();
      } else {
        protocolInfo = device.getGatewayDevice().getProtocolInfo();
      }

      if (protocolInfo == null || !this.protocolRequestService.isSupported(protocolInfo)) {
        if (protocolInfo == null) {
          LOGGER.error("Protocol unknown for device [{}]", device.getDeviceIdentification());
        } else {
          LOGGER.error(
              "Protocol [{}] with version [{}] unknown for device [{}], needs to be reloaded.",
              protocolInfo.getProtocol(),
              protocolInfo.getProtocolVersion(),
              device.getDeviceIdentification());
        }

        throw new FunctionalException(
            FunctionalExceptionType.PROTOCOL_UNKNOWN_FOR_DEVICE, ComponentType.OSGP_CORE);
      }

      LOGGER.info(
          "Device is using protocol [{}] with version [{}]",
          protocolInfo.getProtocol(),
          protocolInfo.getProtocolVersion());

      final Organisation organisation =
          this.domainHelperService.findOrganisation(message.getOrganisationIdentification());

      this.domainHelperService.isAllowed(
          organisation, device, Enum.valueOf(DeviceFunction.class, message.getMessageType()));

      this.protocolRequestService.send(message, protocolInfo);

    } catch (final FunctionalException e) {
      this.domainResponseMessageSender.send(message, e);
      throw e;
    }
  }
}
