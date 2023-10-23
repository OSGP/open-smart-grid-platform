// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.services;

import org.opensmartgridplatform.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.valueobjects.IntegrationType;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelResponse;
import org.opensmartgridplatform.dto.da.GetDeviceModelRequestDto;
import org.opensmartgridplatform.dto.da.GetDeviceModelResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "domainDistributionAutomationAdHocManagementService")
public class AdHocManagementService extends BaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);

  @Autowired private DomainDistributionAutomationMapper mapper;

  @Autowired private RtuResponseService rtuResponseService;

  /** Constructor */
  public AdHocManagementService() {
    // Parameterless constructor required for transactions...
  }

  public void getDeviceModel(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final GetDeviceModelRequest request)
      throws FunctionalException {

    LOGGER.info(
        "Get device model for device [{}] with correlation id [{}]",
        deviceIdentification,
        correlationUid);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final GetDeviceModelRequestDto dto = this.mapper.map(request, GetDeviceModelRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto),
        messageType,
        device.getNetworkAddress());
  }

  public void handleGetDeviceModelResponse(
      final GetDeviceModelResponseDto getDeviceModelResponseDto,
      final CorrelationIds correlationIds,
      final String messageType,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    final String deviceIdentification = correlationIds.getDeviceIdentification();
    final String organisationIdentification = correlationIds.getOrganisationIdentification();
    final String correlationUid = correlationIds.getCorrelationUid();

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    GetDeviceModelResponse getDeviceModelResponse = null;
    OsgpException exception = osgpException;

    try {
      if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      this.rtuResponseService.handleResponseMessageReceived(LOGGER, deviceIdentification, true);

      getDeviceModelResponse =
          this.mapper.map(getDeviceModelResponseDto, GetDeviceModelResponse.class);

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      exception =
          this.ensureOsgpException(
              e, "Exception occurred while getting Device Model Response Data");
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withCorrelationUid(correlationUid)
            .withOrganisationIdentification(organisationIdentification)
            .withDeviceIdentification(deviceIdentification)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(getDeviceModelResponse)
            .build();
    this.responseMessageRouter.send(responseMessage, messageType);
  }

  public void handleGetDataResponse(final ResponseMessage response, final MessageType messageType) {

    final String deviceIdentification = response.getDeviceIdentification();
    LOGGER.info(
        "Forward {} response {} for device: {}", messageType, response, deviceIdentification);

    final boolean deviceIsKnown;
    try {
      // For GET_DATA responses device data is not always expected to be known to GXF.
      deviceIsKnown =
          this.rtuResponseService.handleResponseMessageReceived(
              LOGGER, deviceIdentification, false);
    } catch (final FunctionalException e) {
      LOGGER.error("FunctionalException", e);
      return;
    }

    /*
     * Make sure KAFKA routing is used when the device that is not necessarily expected to be known,
     * is actually unknown. Otherwise let the responseMessageRouter do its regular work based on the
     * known device identified in the responseMessage.
     */
    if (deviceIsKnown) {
      this.responseMessageRouter.send(response, messageType.toString());
    } else {
      this.responseMessageRouter.send(response, messageType.toString(), IntegrationType.KAFKA);
    }
  }
}
