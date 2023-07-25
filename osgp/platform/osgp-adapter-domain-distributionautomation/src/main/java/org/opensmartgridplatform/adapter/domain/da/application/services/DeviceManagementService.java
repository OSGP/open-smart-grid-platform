// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.services;

import org.opensmartgridplatform.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.AddRtuDeviceRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse;
import org.opensmartgridplatform.dto.da.GetHealthStatusRequestDto;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "domainDistributionAutomationDeviceManagementService")
public class DeviceManagementService extends BaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementService.class);

  @Autowired private DomainDistributionAutomationMapper mapper;

  @Autowired private RtuDeviceService rtuDeviceService;

  @Autowired private RtuResponseService rtuResponseService;

  /** Constructor */
  public DeviceManagementService() {
    // Parameterless constructor required for transactions...
  }

  public void getHealthStatus(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final GetHealthStatusRequest getHealthStatusRequest)
      throws FunctionalException {

    LOGGER.info(
        "Get Health Status for device [{}] with correlation id [{}]",
        deviceIdentification,
        correlationUid);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final GetHealthStatusRequestDto dto =
        this.mapper.map(getHealthStatusRequest, GetHealthStatusRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto),
        messageType,
        device.getNetworkAddress());
  }

  public void handleInternalHealthStatusResponse(
      final GetHealthStatusResponseDto getHealthStatusResponseDto,
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final String messageType) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    final GetHealthStatusResponse getHealthStatusResponse =
        this.mapper.map(getHealthStatusResponseDto, GetHealthStatusResponse.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withMessageType(messageType)
            .withResult(ResponseMessageResultType.OK)
            .withDataObject(getHealthStatusResponse)
            .build();
    this.responseMessageRouter.send(responseMessage, messageType);
  }

  public void handleHealthStatusResponse(
      final GetHealthStatusResponseDto getHealthStatusResponseDto,
      final String deviceIdentification,
      final String organisationIdentification,
      final String correlationUid,
      final String messageType,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    GetHealthStatusResponse getHealthStatusResponse = null;
    OsgpException exception = null;

    try {
      if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      this.rtuResponseService.handleResponseMessageReceived(LOGGER, deviceIdentification, true);

      getHealthStatusResponse =
          this.mapper.map(getHealthStatusResponseDto, GetHealthStatusResponse.class);

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      exception =
          this.ensureOsgpException(
              e, "Exception occurred while getting Health Status Response Data");
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withMessageType(messageType)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(getHealthStatusResponse)
            .build();
    this.responseMessageRouter.send(responseMessage, messageType);
  }

  public void addDevice(
      final MessageMetadata deviceMessageMetadata, final AddRtuDeviceRequest addRtuDeviceRequest)
      throws FunctionalException {
    final String organisationIdentification = deviceMessageMetadata.getOrganisationIdentification();
    final String deviceIdentification = deviceMessageMetadata.getDeviceIdentification();
    LOGGER.debug(
        "addDevice for organisationIdentification: {} for deviceIdentification: {}",
        organisationIdentification,
        deviceIdentification);
    this.rtuDeviceService.storeRtuDevice(organisationIdentification, addRtuDeviceRequest);
  }
}
