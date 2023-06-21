// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.PendingRequestsQueue;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.EventTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Optionals;
import org.springframework.stereotype.Service;

@Service
public class LightSensorDeviceResponseService extends AbstractDeviceResponseService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LightSensorDeviceResponseService.class);

  private static final String ERROR_PREFIX =
      "Error while processing measurement report for light sensor:";

  private static final DeviceType DEVICE_TYPE = DeviceType.LIGHT_SENSOR;

  @Autowired private Iec60870DeviceRepository iec60870DeviceRepository;

  @Autowired private LightMeasurementService lightMeasurementService;

  @Autowired private PendingRequestsQueue pendingRequestsQueue;

  public LightSensorDeviceResponseService() {
    super(DEVICE_TYPE);
  }

  @Override
  public void process(
      final MeasurementReportDto measurementReport, final ResponseMetadata responseMetadata) {
    final String deviceIdentification = responseMetadata.getDeviceIdentification();
    this.logReceivedMeasurementReport(
        measurementReport, "measurement report", deviceIdentification);
    this.findDeviceAndThen(
        deviceIdentification,
        device ->
            this.sendLightSensorStatusResponse(
                measurementReport, device, responseMetadata, ERROR_PREFIX),
        () -> this.logDeviceNotFound(deviceIdentification, ERROR_PREFIX));
  }

  @Override
  public void processEvent(
      final MeasurementReportDto measurementReport, final ResponseMetadata responseMetadata) {
    final String deviceIdentification = responseMetadata.getDeviceIdentification();
    this.logReceivedMeasurementReport(measurementReport, "event", deviceIdentification);
    this.findDeviceAndThen(
        deviceIdentification,
        device -> this.sendEvent(measurementReport, device, responseMetadata, ERROR_PREFIX),
        () -> this.logDeviceNotFound(deviceIdentification, ERROR_PREFIX));
  }

  private void logReceivedMeasurementReport(
      final MeasurementReportDto measurementReport,
      final String description,
      final String deviceIdentification) {
    LOGGER.info(
        "Received {} {} for light sensor {}.",
        description,
        measurementReport,
        deviceIdentification);
  }

  private void logDeviceNotFound(final String deviceIdentification, final String errorPrefix) {
    LOGGER.error("{} Light sensor {} not found.", errorPrefix, deviceIdentification);
  }

  private void findDeviceAndThen(
      final String deviceIdentification,
      final Consumer<? super Iec60870Device> actionWithDevice,
      final Runnable actionWhenDeviceNotFound) {

    final Optional<Iec60870Device> optionalDevice =
        this.iec60870DeviceRepository.findByDeviceIdentification(deviceIdentification);
    optionalDevice.ifPresent(actionWithDevice);
    if (!optionalDevice.isPresent()) {
      actionWhenDeviceNotFound.run();
    }
  }

  public void sendLightSensorStatusResponse(
      final MeasurementReportDto measurementReportDto,
      final Iec60870Device device,
      final ResponseMetadata responseMetadata,
      final String errorPrefix) {

    /*
     * If the device has a gateway device, the report contains values for
     * all the devices of the gateway device. We are going to handle them
     * all.
     */
    if (device.hasGatewayDevice()) {
      /*
       * A question that may arise here, is why go through all devices
       * behind the gateway, instead of sending a response just for the
       * device provided in the method parameter.
       *
       * Due to the way the IEC 60870-5-104 protocol works at the moment
       * this code is added, the device from the method parameter may not
       * actually be the device for which the light sensor status was
       * requested.
       *
       * The proper device is filtered out later in the call chain in
       * sendOrLogLightSensorStatus. If a correlation UID has been queued
       * for the light measurement device, a response will be sent for
       * that device and the then dequeued correlation UID.
       */
      this.iec60870DeviceRepository
          .findByGatewayDeviceIdentification(device.getGatewayDeviceIdentification())
          .forEach(
              d ->
                  this.findMeasurementGroupAndThen(
                      measurementReportDto,
                      d,
                      measurementGroup ->
                          this.sendLightSensorStatusResponse(
                              measurementGroup, d, responseMetadata, errorPrefix),
                      () -> this.logMeasurementGroupNotFound(d, errorPrefix)));
    } else {
      this.findMeasurementGroupAndThen(
          measurementReportDto,
          device,
          measurementGroup ->
              this.sendLightSensorStatusResponse(
                  measurementGroup, device, responseMetadata, errorPrefix),
          () -> this.logMeasurementGroupNotFound(device, errorPrefix));
    }
  }

  public void sendEvent(
      final MeasurementReportDto measurementReportDto,
      final Iec60870Device device,
      final ResponseMetadata responseMetadata,
      final String errorPrefix) {

    this.findMeasurementGroupAndThen(
        measurementReportDto,
        device,
        measurementGroup -> this.sendEvent(measurementGroup, device, responseMetadata, errorPrefix),
        () -> this.logMeasurementGroupNotFound(device, errorPrefix));
  }

  private void logMeasurementGroupNotFound(final Iec60870Device device, final String errorPrefix) {
    LOGGER.error(
        "{} MeasurementGroup {} not found for device {}",
        errorPrefix,
        device.getInformationObjectAddress(),
        device.getDeviceIdentification());
  }

  private void findMeasurementGroupAndThen(
      final MeasurementReportDto measurementReportDto,
      final Iec60870Device device,
      final Consumer<? super MeasurementGroupDto> actionWithMeasurementGroup,
      final Runnable actionWhenMeasurementGroupNotFound) {

    final Optional<MeasurementGroupDto> optionalMeasurementGroup =
        this.findMeasurementGroupForDevice(measurementReportDto, device);
    optionalMeasurementGroup.ifPresent(actionWithMeasurementGroup);
    if (!optionalMeasurementGroup.isPresent()) {
      actionWhenMeasurementGroupNotFound.run();
    }
  }

  private Optional<MeasurementGroupDto> findMeasurementGroupForDevice(
      final MeasurementReportDto measurementReportDto, final Iec60870Device device) {

    final String informationObjectAddress = String.valueOf(device.getInformationObjectAddress());
    final Predicate<MeasurementGroupDto> isGroupForInformationObjectAddress =
        group -> informationObjectAddress.equals(group.getIdentification());
    return measurementReportDto.getMeasurementGroups().stream()
        .filter(isGroupForInformationObjectAddress)
        .findFirst();
  }

  private void sendLightSensorStatusResponse(
      final MeasurementGroupDto measurementGroup,
      final Iec60870Device device,
      final ResponseMetadata responseMetadata,
      final String errorPrefix) {

    this.findLightSensorStatusAndThen(
        measurementGroup,
        lightSensorStatus ->
            this.sendOrLogLightSensorStatus(lightSensorStatus, device, responseMetadata),
        () ->
            LOGGER.error(
                "{} Light sensor status information not found for device {}",
                errorPrefix,
                device.getDeviceIdentification()));
  }

  private void sendOrLogLightSensorStatus(
      final LightSensorStatusDto lightSensorStatus,
      final Iec60870Device device,
      final ResponseMetadata responseMetadata) {

    final String deviceIdentification = device.getDeviceIdentification();

    Optionals.ifPresentOrElse(
        this.pendingRequestsQueue.dequeue(deviceIdentification),
        uid ->
            this.sendLightSensorStatus(
                uid, lightSensorStatus, deviceIdentification, responseMetadata),
        () ->
            LOGGER.info(
                "No correlation UID found for device identification {}", deviceIdentification));
  }

  private void sendLightSensorStatus(
      final String correlationUid,
      final LightSensorStatusDto lightSensorStatus,
      final String deviceIdentification,
      final ResponseMetadata responseMetadata) {

    final ResponseMetadata rm =
        new ResponseMetadata.Builder()
            .withCorrelationUid(correlationUid)
            .withDeviceIdentification(deviceIdentification)
            .withDomainInfo(responseMetadata.getDomainInfo())
            .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
            .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
            .build();

    this.lightMeasurementService.sendSensorStatus(lightSensorStatus, rm);
  }

  private void findLightSensorStatusAndThen(
      final MeasurementGroupDto measurementGroup,
      final Consumer<? super LightSensorStatusDto> actionWithLightSensorStatus,
      final Runnable actionWhenLightSensorStatusNotFound) {

    final Optional<LightSensorStatusDto> optionalLightSensorStatus =
        this.findLightSensorStatus(measurementGroup);
    optionalLightSensorStatus.ifPresent(actionWithLightSensorStatus);
    if (!optionalLightSensorStatus.isPresent()) {
      actionWhenLightSensorStatusNotFound.run();
    }
  }

  private Optional<LightSensorStatusDto> findLightSensorStatus(
      final MeasurementGroupDto measurementGroup) {
    return this.findSensorValue(measurementGroup)
        .map(this::determineLightSensorStatus)
        .map(LightSensorStatusDto::new);
  }

  private void sendEvent(
      final MeasurementGroupDto measurementGroup,
      final Iec60870Device device,
      final ResponseMetadata responseMetadata,
      final String errorPrefix) {

    this.findEventNotificationAndThen(
        measurementGroup,
        device,
        eventNotification -> this.sendEvent(eventNotification, device, responseMetadata),
        () ->
            LOGGER.error(
                "{} Event information not found for device {}",
                errorPrefix,
                device.getDeviceIdentification()));
  }

  private void sendEvent(
      final EventNotificationDto eventNotification,
      final Iec60870Device device,
      final ResponseMetadata responseMetadata) {

    final ResponseMetadata rm =
        new ResponseMetadata.Builder()
            .withCorrelationUid(responseMetadata.getCorrelationUid())
            .withDeviceIdentification(device.getDeviceIdentification())
            .withDomainInfo(responseMetadata.getDomainInfo())
            .withMessageType(MessageType.EVENT_NOTIFICATION.name())
            .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
            .build();

    this.lightMeasurementService.sendEventNotification(eventNotification, rm);
  }

  private void findEventNotificationAndThen(
      final MeasurementGroupDto measurementGroup,
      final Iec60870Device device,
      final Consumer<? super EventNotificationDto> actionWithEventNotification,
      final Runnable actionWhenEventNotificationNotFound) {

    final Optional<EventNotificationDto> optionalEventNotification =
        this.findEventNotification(measurementGroup, device);
    optionalEventNotification.ifPresent(actionWithEventNotification);
    if (!optionalEventNotification.isPresent()) {
      actionWhenEventNotificationNotFound.run();
    }
  }

  private EventTypeDto determineLightSensorEventType(final boolean on) {
    return on ? EventTypeDto.LIGHT_SENSOR_REPORTS_DARK : EventTypeDto.LIGHT_SENSOR_REPORTS_LIGHT;
  }

  private LightSensorStatusTypeDto determineLightSensorStatus(final boolean on) {
    return on ? LightSensorStatusTypeDto.DARK : LightSensorStatusTypeDto.LIGHT;
  }

  private Optional<EventNotificationDto> findEventNotification(
      final MeasurementGroupDto measurementGroup, final Iec60870Device device) {

    final String deviceUid = device.getDeviceIdentification();
    final int index = device.getInformationObjectAddress();

    final EventTypeDto eventType =
        this.findSensorValue(measurementGroup)
            .map(this::determineLightSensorEventType)
            .orElse(null);
    if (eventType == null) {
      LOGGER.warn(
          "Unable to determine event type for event notification for device {}",
          device.getDeviceIdentification());
    }

    final DateTime dateTime = this.findTimeValue(measurementGroup).orElse(null);
    if (dateTime == null) {
      LOGGER.warn(
          "Unable to determine the time for event notification for device {}",
          device.getDeviceIdentification());
    }

    final EventNotificationDto eventNotification;
    if (eventType == null || dateTime == null) {
      eventNotification = null;
    } else {
      eventNotification =
          new EventNotificationDto(deviceUid, dateTime, eventType, "Spontaneous report", index);
    }
    return Optional.ofNullable(eventNotification);
  }

  private Optional<Boolean> findSensorValue(final MeasurementGroupDto measurementGroup) {
    return measurementGroup.getMeasurements().stream()
        .flatMap(measurement -> measurement.getMeasurementElements().stream())
        .filter(element -> element instanceof BitmaskMeasurementElementDto)
        .map(element -> ((BitmaskMeasurementElementDto) element).getValue() == 1)
        .findFirst();
  }

  private Optional<DateTime> findTimeValue(final MeasurementGroupDto measurementGroup) {
    return measurementGroup.getMeasurements().stream()
        .flatMap(measurement -> measurement.getMeasurementElements().stream())
        .filter(element -> element instanceof TimestampMeasurementElementDto)
        .map(element -> ((TimestampMeasurementElementDto) element).getValue())
        .map(DateTime::new)
        .findFirst();
  }
}
