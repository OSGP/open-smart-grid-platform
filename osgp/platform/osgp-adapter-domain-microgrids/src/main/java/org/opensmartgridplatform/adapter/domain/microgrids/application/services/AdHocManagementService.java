// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.microgrids.application.services;

import java.time.Duration;
import java.time.Instant;
import javax.persistence.OptimisticLockException;
import org.opensmartgridplatform.adapter.domain.microgrids.application.mapping.DomainMicrogridsMapper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.UnknownEntityException;
import org.opensmartgridplatform.domain.microgrids.valueobjects.EmptyResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataRequest;
import org.opensmartgridplatform.domain.microgrids.valueobjects.GetDataResponse;
import org.opensmartgridplatform.domain.microgrids.valueobjects.SetDataRequest;
import org.opensmartgridplatform.dto.valueobjects.microgrids.EmptyResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataRequestDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.GetDataResponseDto;
import org.opensmartgridplatform.dto.valueobjects.microgrids.SetDataRequestDto;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderUUIDService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service(value = "domainMicrogridsAdHocManagementService")
@Transactional(value = "transactionManager")
public class AdHocManagementService extends BaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AdHocManagementService.class);
  private static final ComponentType COMPONENT_TYPE = ComponentType.DOMAIN_MICROGRIDS;

  @Autowired private DomainMicrogridsMapper mapper;

  @Autowired private CorrelationIdProviderUUIDService correlationIdProviderUUIDService;

  @Value(
      "#{T(java.time.Duration).parse('${communication.monitoring.minimum.duration.between.communication.time.updates:PT1M}')}")
  private Duration minimumDurationBetweenCommunicationTimeUpdates;

  /** Constructor */
  public AdHocManagementService() {
    // Parameterless constructor required for transactions...
  }

  // === GET DATA ===

  public void getData(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final GetDataRequest dataRequest)
      throws FunctionalException {

    LOGGER.info(
        "Get data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final GetDataRequestDto dto = this.mapper.map(dataRequest, GetDataRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto),
        messageType,
        device.getIpAddress());
  }

  public void handleInternalDataResponse(
      final GetDataResponseDto dataResponseDto,
      final CorrelationIds ids,
      final String messageType) {
    LOGGER.info("handleInternalDataResponse for MessageType: {}", messageType);
    final GetDataResponse dataResponse = this.mapper.map(dataResponseDto, GetDataResponse.class);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(ResponseMessageResultType.OK)
            .withMessageType(messageType)
            .withDataObject(dataResponse)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageType);
  }

  public void handleGetDataResponse(
      final GetDataResponseDto dataResponseDto,
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    GetDataResponse dataResponse = null;
    OsgpException exception = null;

    try {
      if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      this.handleResponseMessageReceived(ids.getDeviceIdentification());

      dataResponse = this.mapper.map(dataResponseDto, GetDataResponse.class);

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      exception = this.ensureOsgpException(e, "Exception occurred while getting data");
    }

    // Support for Push messages, generate correlationUid
    String actualCorrelationUid = ids.getCorrelationUid();
    if ("no-correlationUid".equals(actualCorrelationUid)) {
      actualCorrelationUid =
          this.correlationIdProviderUUIDService.getCorrelationId(
              "DeviceGenerated", ids.getDeviceIdentification());
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withCorrelationUid(actualCorrelationUid)
            .withMessageType(messageType)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(dataResponse)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageType);
  }

  // === SET DATA ===

  public void handleSetDataRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final String messageType,
      final SetDataRequest setDataRequest)
      throws FunctionalException {

    LOGGER.info(
        "Set data for device [{}] with correlation id [{}]", deviceIdentification, correlationUid);

    this.findOrganisation(organisationIdentification);
    final Device device = this.findActiveDevice(deviceIdentification);

    final SetDataRequestDto dto = this.mapper.map(setDataRequest, SetDataRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(correlationUid, organisationIdentification, deviceIdentification, dto),
        messageType,
        device.getIpAddress());
  }

  public void handleSetDataResponse(
      final EmptyResponseDto emptyResponseDto,
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    EmptyResponse emptyResponse = null;
    OsgpException exception = null;

    try {
      if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      this.handleResponseMessageReceived(ids.getDeviceIdentification());

      emptyResponse = this.mapper.map(emptyResponseDto, EmptyResponse.class);

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      exception = this.ensureOsgpException(e, "Exception occurred while setting data");
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(emptyResponse)
            .build();
    this.webServiceResponseMessageSender.send(responseMessage, messageType);
  }

  private void handleResponseMessageReceived(final String deviceIdentification)
      throws FunctionalException {
    try {
      final RtuDevice device =
          this.rtuDeviceRepository
              .findByDeviceIdentification(deviceIdentification)
              .orElseThrow(
                  () ->
                      new FunctionalException(
                          FunctionalExceptionType.UNKNOWN_DEVICE,
                          COMPONENT_TYPE,
                          new UnknownEntityException(RtuDevice.class, deviceIdentification)));
      if (this.shouldUpdateCommunicationTime(device)) {
        device.messageReceived();
        this.rtuDeviceRepository.save(device);
      } else {
        LOGGER.info(
            "Last communication time within duration: {}. Skipping last communication date update.",
            this.minimumDurationBetweenCommunicationTimeUpdates);
      }
    } catch (final OptimisticLockException ex) {
      LOGGER.warn("Last communication time not updated due to optimistic lock exception", ex);
    }
  }

  private boolean shouldUpdateCommunicationTime(final RtuDevice device) {
    final Instant timeToCheck =
        Instant.now().minus(this.minimumDurationBetweenCommunicationTimeUpdates);
    final Instant timeOfLastCommunication = device.getLastCommunicationTime();
    return timeOfLastCommunication.isBefore(timeToCheck);
  }
}
