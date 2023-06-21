// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.services;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling.ResponseNotFoundException;
import org.opensmartgridplatform.adapter.ws.da.infra.jms.DistributionAutomationRequestMessage;
import org.opensmartgridplatform.adapter.ws.da.infra.jms.DistributionAutomationRequestMessageSender;
import org.opensmartgridplatform.adapter.ws.domain.entities.ResponseData;
import org.opensmartgridplatform.adapter.ws.shared.services.ResponseDataService;
import org.opensmartgridplatform.domain.core.entities.Organisation;
import org.opensmartgridplatform.domain.core.entities.RtuDevice;
import org.opensmartgridplatform.domain.core.exceptions.ArgumentNullOrEmptyException;
import org.opensmartgridplatform.domain.core.valueobjects.AddRtuDeviceRequest;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetDeviceModelResponse;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetHealthStatusResponse;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse;
import org.opensmartgridplatform.shared.domain.services.CorrelationIdProviderService;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.validation.Identification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Transactional(value = "transactionManager")
@Validated
public class DistributionAutomationService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DistributionAutomationService.class);

  @Autowired private DomainHelperService domainHelperService;

  @Autowired private CorrelationIdProviderService correlationIdProviderService;

  @Autowired private DistributionAutomationRequestMessageSender requestMessageSender;

  @Autowired private ResponseDataService responseDataService;

  public DistributionAutomationService() {
    // Parameterless constructor required for transactions
  }

  public String enqueueGetPQValuesRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      @NotNull final GetPQValuesRequest getPQValuesRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueGetPQValuesRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    return this.processRequest(
        organisationIdentification,
        deviceIdentification,
        getPQValuesRequest,
        DeviceFunction.GET_POWER_QUALITY_VALUES,
        MessageType.GET_POWER_QUALITY_VALUES);
  }

  public GetPQValuesResponse dequeueGetPQValuesResponse(final String correlationUid)
      throws OsgpException {

    LOGGER.debug("dequeueGetPQValuesResponse called with correlation uid {}", correlationUid);

    return (GetPQValuesResponse) this.processResponse(correlationUid);
  }

  public String enqueueGetPQValuesPeriodicRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final GetPQValuesPeriodicRequest getPQValuesPeriodicRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueGetPQValuesPeriodicRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    return this.processRequest(
        organisationIdentification,
        deviceIdentification,
        getPQValuesPeriodicRequest,
        DeviceFunction.GET_POWER_QUALITY_VALUES,
        MessageType.GET_POWER_QUALITY_VALUES_PERIODIC);
  }

  public GetPQValuesResponse dequeueGetPQValuesPeriodicResponse(final String correlationUid)
      throws OsgpException {

    LOGGER.debug(
        "dequeueGetPQValuesPeriodicResponse called with correlation uid {}", correlationUid);
    return (GetPQValuesResponse) this.processResponse(correlationUid);
  }

  public String enqueueGetDeviceModelRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final GetDeviceModelRequest getDeviceModelRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueGetDeviceModelRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);
    return this.processRequest(
        organisationIdentification,
        deviceIdentification,
        getDeviceModelRequest,
        DeviceFunction.GET_DEVICE_MODEL,
        MessageType.GET_DEVICE_MODEL);
  }

  public GetDeviceModelResponse dequeueGetDeviceModelResponse(final String correlationUid)
      throws OsgpException {

    LOGGER.debug("dequeueGetDeviceModelResponse called with correlation uid {}", correlationUid);
    return (GetDeviceModelResponse) this.processResponse(correlationUid);
  }

  public String enqueueGetHealthStatusRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final GetHealthStatusRequest getHealthStatusRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueGetHealthStatusRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);
    return this.processRequest(
        organisationIdentification,
        deviceIdentification,
        getHealthStatusRequest,
        DeviceFunction.GET_HEALTH_STATUS,
        MessageType.GET_HEALTH_STATUS);
  }

  public GetHealthStatusResponse dequeueGetHealthResponse(final String correlationUid)
      throws OsgpException {

    LOGGER.debug("dequeueGetHealthResponse called with correlation uid {}", correlationUid);
    return (GetHealthStatusResponse) this.processResponse(correlationUid);
  }

  public MeasurementReport dequeueMeasurementReport(final String correlationUid)
      throws OsgpException {

    LOGGER.debug("dequeueMeasurementReport called with correlation uid {}", correlationUid);
    return (MeasurementReport) this.processResponse(correlationUid);
  }

  public ResponseData dequeueResponseData(final String correlationUid) throws OsgpException {
    LOGGER.debug("dequeueResponseData called with correlation uid {}", correlationUid);
    return (ResponseData) this.processResponse(correlationUid);
  }

  public String enqueueAddRtuDeviceRequest(
      @Identification final String organisationIdentification,
      @Identification final String deviceIdentification,
      final AddRtuDeviceRequest addRtuDeviceRequest)
      throws OsgpException {

    LOGGER.debug(
        "enqueueAddRtuDeviceRequest called with organisation {} and device {}",
        organisationIdentification,
        deviceIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final DistributionAutomationRequestMessage message =
        new DistributionAutomationRequestMessage(
            MessageType.ADD_DEVICE,
            correlationUid,
            organisationIdentification,
            deviceIdentification,
            addRtuDeviceRequest);

    try {
      this.requestMessageSender.send(message);
    } catch (final ArgumentNullOrEmptyException e) {
      throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, e);
    }

    return correlationUid;
  }

  private String processRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final Serializable request,
      final DeviceFunction deviceFunction,
      final MessageType messageType)
      throws OsgpException {
    final Organisation organisation =
        this.domainHelperService.findOrganisation(organisationIdentification);

    final String correlationUid =
        this.correlationIdProviderService.getCorrelationId(
            organisationIdentification, deviceIdentification);

    final RtuDevice device = this.domainHelperService.findDevice(deviceIdentification);
    this.domainHelperService.isAllowed(organisation, device, deviceFunction);

    final DistributionAutomationRequestMessage message =
        new DistributionAutomationRequestMessage(
            messageType, correlationUid, organisationIdentification, deviceIdentification, request);

    try {
      this.requestMessageSender.send(message);
    } catch (final ArgumentNullOrEmptyException e) {
      throw new TechnicalException(ComponentType.WS_DISTRIBUTION_AUTOMATION, e);
    }
    return correlationUid;
  }

  private Serializable processResponse(final String correlationUid) throws OsgpException {
    final ResponseData responseData =
        this.responseDataService.dequeue(
            correlationUid, ResponseMessage.class, ComponentType.WS_DISTRIBUTION_AUTOMATION);
    final ResponseMessage response = (ResponseMessage) responseData.getMessageData();

    switch (response.getResult()) {
      case OK:
        if (response.getDataObject() != null) {
          return response.getDataObject();
        }
        // Should not get here
        throw new TechnicalException(
            ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message contains no data.");
      case NOT_FOUND:
        throw new ResponseNotFoundException(
            ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message not found.");
      case NOT_OK:
        if (response.getOsgpException() != null) {
          throw response.getOsgpException();
        }
        throw new TechnicalException(
            ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message not ok.");
      default:
        // Should not get here
        throw new TechnicalException(
            ComponentType.WS_DISTRIBUTION_AUTOMATION, "Response message contains invalid result.");
    }
  }
}
