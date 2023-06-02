//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.da.application.services;

import org.opensmartgridplatform.adapter.domain.da.application.mapping.DomainDistributionAutomationMapper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.da.measurements.MeasurementReport;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesPeriodicRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesRequest;
import org.opensmartgridplatform.domain.da.valueobjects.GetPQValuesResponse;
import org.opensmartgridplatform.dto.da.GetPQValuesPeriodicRequestDto;
import org.opensmartgridplatform.dto.da.GetPQValuesRequestDto;
import org.opensmartgridplatform.dto.da.GetPQValuesResponseDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.CorrelationIds;
import org.opensmartgridplatform.shared.infra.jms.RequestMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessage;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "domainDistributionAutomationMonitoringService")
public class MonitoringService extends BaseService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

  @Autowired private DomainDistributionAutomationMapper mapper;

  @Autowired private RtuResponseService rtuResponseService;

  /** Constructor */
  public MonitoringService() {
    // Parameterless constructor required for transactions...
  }

  public void getPQValues(
      final CorrelationIds ids,
      final String messageType,
      final GetPQValuesRequest getPQValuesRequest)
      throws FunctionalException {

    LOGGER.info(
        "Get PQ Values for device [{}] with correlation id [{}]",
        ids.getDeviceIdentification(),
        ids.getCorrelationUid());

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());

    final GetPQValuesRequestDto dto =
        this.mapper.map(getPQValuesRequest, GetPQValuesRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(ids, dto), messageType, device.getIpAddress());
  }

  public void getPQValuesPeriodic(
      final CorrelationIds ids,
      final String messageType,
      final GetPQValuesPeriodicRequest getPQValuesPeriodicRequest)
      throws FunctionalException {

    LOGGER.info(
        "Get PQ Values periodic for device [{}] with correlation id [{}]",
        ids.getDeviceIdentification(),
        ids.getCorrelationUid());

    this.findOrganisation(ids.getOrganisationIdentification());
    final Device device = this.findActiveDevice(ids.getDeviceIdentification());

    final GetPQValuesPeriodicRequestDto dto =
        this.mapper.map(getPQValuesPeriodicRequest, GetPQValuesPeriodicRequestDto.class);

    this.osgpCoreRequestMessageSender.send(
        new RequestMessage(ids, dto), messageType, device.getIpAddress());
  }

  public void handleGetPQValuesResponse(
      final GetPQValuesResponseDto getPQValuesResponseDto,
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    final String deviceIdentification = ids.getDeviceIdentification();
    final String organisationIdentification = ids.getOrganisationIdentification();

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    GetPQValuesResponse getPQValuesResponse = null;
    OsgpException exception = osgpException;

    try {
      if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      this.rtuResponseService.handleResponseMessageReceived(LOGGER, deviceIdentification, true);

      getPQValuesResponse = this.mapper.map(getPQValuesResponseDto, GetPQValuesResponse.class);

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      exception =
          this.ensureOsgpException(e, "Exception occurred while getting PQ Values Response Data");
    }

    // Support for Push messages, generate correlationUid
    String actualCorrelationUid = ids.getCorrelationUid();
    if ("no-correlationUid".equals(actualCorrelationUid)) {
      actualCorrelationUid = getCorrelationId("DeviceGenerated", deviceIdentification);
    }

    final CorrelationIds actualIds =
        new CorrelationIds(organisationIdentification, deviceIdentification, actualCorrelationUid);

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(actualIds)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(getPQValuesResponse)
            .build();
    this.responseMessageRouter.send(responseMessage, messageType);
  }

  public void handleGetMeasurementReportResponse(
      final MeasurementReportDto measurementReportDto,
      final CorrelationIds ids,
      final String messageType,
      final ResponseMessageResultType responseMessageResultType,
      final OsgpException osgpException) {

    LOGGER.info("handleResponse for MessageType: {}", messageType);

    ResponseMessageResultType result = ResponseMessageResultType.OK;
    MeasurementReport measurementReport = null;
    OsgpException exception = osgpException;

    try {
      if (responseMessageResultType == ResponseMessageResultType.NOT_OK || osgpException != null) {
        LOGGER.error("Device Response not ok.", osgpException);
        throw osgpException;
      }

      this.rtuResponseService.handleResponseMessageReceived(
          LOGGER, ids.getDeviceIdentification(), true);

      measurementReport = this.mapper.map(measurementReportDto, MeasurementReport.class);

    } catch (final Exception e) {
      LOGGER.error("Unexpected Exception", e);
      result = ResponseMessageResultType.NOT_OK;
      exception =
          this.ensureOsgpException(e, "Exception occurred while receiving Measurement Report");
    }

    final ResponseMessage responseMessage =
        ResponseMessage.newResponseMessageBuilder()
            .withIds(ids)
            .withResult(result)
            .withOsgpException(exception)
            .withDataObject(measurementReport)
            .build();
    this.responseMessageRouter.send(responseMessage, messageType);
  }
}
