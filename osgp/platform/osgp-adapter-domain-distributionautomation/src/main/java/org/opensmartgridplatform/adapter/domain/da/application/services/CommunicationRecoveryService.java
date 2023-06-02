//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.application.services;

import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service(value = "domainDistributionAutomationCommunicationRecoveryService")
public class CommunicationRecoveryService extends BaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationRecoveryService.class);

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired
  @Qualifier("domainDistributionAutomationDeviceManagementService")
  private DeviceManagementService deviceManagementService;

  /**
   * Send a signal that the connection with the device has been lost. This is done by putting a
   * GetHealthStatus on the queue with an alarm value. When this response is received by the
   * webservice adapter, it can send a notification to the client.
   *
   * @param rtu
   */
  public void signalConnectionLost(final RtuDevice rtu) {
    LOGGER.info("Sending connection lost signal for device {}.", rtu.getDeviceIdentification());

    final GetHealthStatusResponseDto getHealthStatusResponseDto =
        new GetHealthStatusResponseDto("NOTRESPONDING");

    final String correlationUid = this.createCorrelationUid(rtu);
    final String organisationIdentification = rtu.getOwner().getOrganisationIdentification();
    final String deviceIdentification = rtu.getDeviceIdentification();

    this.deviceManagementService.handleInternalHealthStatusResponse(
        getHealthStatusResponseDto,
        deviceIdentification,
        organisationIdentification,
        correlationUid,
        DeviceFunction.GET_DATA.toString());
  }

  public void restoreCommunication(final RtuDevice rtu) {
    LOGGER.info("Restoring communication for device {}.", rtu.getDeviceIdentification());

    if (rtu.getOwner() == null) {
      LOGGER.warn(
          "Device {} has no owner. Skipping communication recovery.",
          rtu.getDeviceIdentification());
      return;
    }

    final RequestMessage message = this.createMessage(rtu);
    this.osgpCoreRequestMessageSender.send(
        message, DeviceFunction.GET_DATA.toString(), rtu.getIpAddress());
  }

  private RequestMessage createMessage(final RtuDevice rtu) {
    LOGGER.debug("Creating message for device {}.", rtu.getDeviceIdentification());

    final String correlationUid = this.createCorrelationUid(rtu);
    final String organisationIdentification = rtu.getOwner().getOrganisationIdentification();
    final String deviceIdentification = rtu.getDeviceIdentification();
    final GetHealthStatusRequestDto request = this.createHalthStatusRequest(rtu);

    return new RequestMessage(
        correlationUid, organisationIdentification, deviceIdentification, request);
  }

  private String createCorrelationUid(final RtuDevice rtu) {
    LOGGER.debug(
        "Creating correlation uid for device {}, with owner {}",
        rtu.getDeviceIdentification(),
        rtu.getOwner().getOrganisationIdentification());

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            rtu.getOwner().getOrganisationIdentification(), rtu.getDeviceIdentification());

    LOGGER.debug("Correlation uid {} created.", correlationUid);

    return correlationUid;
  }

  private GetHealthStatusRequestDto createHalthStatusRequest(final RtuDevice rtu) {
    LOGGER.debug("Creating Health Status request for rtu {}.", rtu.getDeviceIdentification());
    return new GetHealthStatusRequestDto();
  }
}
