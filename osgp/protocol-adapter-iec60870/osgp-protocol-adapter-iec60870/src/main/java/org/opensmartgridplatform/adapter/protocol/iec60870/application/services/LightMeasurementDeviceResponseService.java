/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementGroupDto;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.opensmartgridplatform.dto.da.measurements.elements.BitmaskMeasurementElementDto;
import org.opensmartgridplatform.dto.da.measurements.elements.TimestampMeasurementElementDto;
import org.opensmartgridplatform.dto.valueobjects.EventNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.EventTypeDto;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.opensmartgridplatform.shared.infra.jms.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LightMeasurementDeviceResponseService extends AbstractDeviceResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(LightMeasurementDeviceResponseService.class);

    private static final String ERROR_PREFIX = "Error while processing measurement report for light measurement device:";

    private static final DeviceType DEVICE_TYPE = DeviceType.LIGHT_MEASUREMENT_DEVICE;

    @Autowired
    private Iec60870DeviceRepository iec60870DeviceRepository;

    @Autowired
    private LightMeasurementService lightMeasurementService;

    public LightMeasurementDeviceResponseService() {
        super(DEVICE_TYPE);
    }

    @Override
    public void process(final MeasurementReportDto measurementReport, final ResponseMetadata responseMetadata) {
        final String deviceIdentification = responseMetadata.getDeviceIdentification();
        this.logReceivedMeasurementReport(measurementReport, "measurement report", deviceIdentification);
        this.findDeviceAndThen(deviceIdentification,
                device -> this.sendLightSensorStatusResponse(measurementReport, device, responseMetadata, ERROR_PREFIX),
                () -> this.logDeviceNotFound(deviceIdentification, ERROR_PREFIX));
    }

    @Override
    public void processEvent(final MeasurementReportDto measurementReport, final ResponseMetadata responseMetadata) {
        final String deviceIdentification = responseMetadata.getDeviceIdentification();
        this.logReceivedMeasurementReport(measurementReport, "event", deviceIdentification);
        this.findDeviceAndThen(deviceIdentification,
                device -> this.sendEvent(measurementReport, device, responseMetadata, ERROR_PREFIX),
                () -> this.logDeviceNotFound(deviceIdentification, ERROR_PREFIX));
    }

    private void logReceivedMeasurementReport(final MeasurementReportDto measurementReport, final String description,
            final String deviceIdentification) {
        LOGGER.info("Received {} {} for light measurement device {}.", description, measurementReport,
                deviceIdentification);
    }

    private void logDeviceNotFound(final String deviceIdentification, final String errorPrefix) {
        LOGGER.error("{} Device {} not found.", errorPrefix, deviceIdentification);
    }

    private void findDeviceAndThen(final String deviceIdentification,
            final Consumer<? super Iec60870Device> actionWithDevice, final Runnable actionWhenDeviceNotFound) {

        final Optional<Iec60870Device> optionalDevice = this.iec60870DeviceRepository
                .findByDeviceIdentification(deviceIdentification);
        optionalDevice.ifPresent(actionWithDevice);
        if (!optionalDevice.isPresent()) {
            actionWhenDeviceNotFound.run();
        }
    }

    public void sendLightSensorStatusResponse(final MeasurementReportDto measurementReportDto,
            final Iec60870Device device, final ResponseMetadata responseMetadata, final String errorPrefix) {

        this.findMeasurementGroupAndThen(
                measurementReportDto, device, measurementGroup -> this.sendLightSensorStatusResponse(measurementGroup,
                        device, responseMetadata, errorPrefix),
                () -> this.logMeasurementGroupNotFound(device, errorPrefix));
    }

    public void sendEvent(final MeasurementReportDto measurementReportDto, final Iec60870Device device,
            final ResponseMetadata responseMetadata, final String errorPrefix) {

        this.findMeasurementGroupAndThen(measurementReportDto, device,
                measurementGroup -> this.sendEvent(measurementGroup, device, responseMetadata, errorPrefix),
                () -> this.logMeasurementGroupNotFound(device, errorPrefix));
    }

    private void logMeasurementGroupNotFound(final Iec60870Device device, final String errorPrefix) {
        LOGGER.error("{} MeasurementGroup {} not found for device {}", errorPrefix,
                device.getInformationObjectAddress(), device.getDeviceIdentification());
    }

    private void findMeasurementGroupAndThen(final MeasurementReportDto measurementReportDto,
            final Iec60870Device device, final Consumer<? super MeasurementGroupDto> actionWithMeasurementGroup,
            final Runnable actionWhenMeasurementGroupNotFound) {

        final Optional<MeasurementGroupDto> optionalMeasurementGroup = this
                .findMeasurementGroupForDevice(measurementReportDto, device);
        optionalMeasurementGroup.ifPresent(actionWithMeasurementGroup);
        if (!optionalMeasurementGroup.isPresent()) {
            actionWhenMeasurementGroupNotFound.run();
        }
    }

    private Optional<MeasurementGroupDto> findMeasurementGroupForDevice(final MeasurementReportDto measurementReportDto,
            final Iec60870Device device) {

        final String informationObjectAddress = String.valueOf(device.getInformationObjectAddress());
        final Predicate<MeasurementGroupDto> isGroupForInformationObjectAddress = group -> informationObjectAddress
                .equals(group.getIdentification());
        return measurementReportDto.getMeasurementGroups()
                .stream()
                .filter(isGroupForInformationObjectAddress)
                .findFirst();
    }

    private void sendLightSensorStatusResponse(final MeasurementGroupDto measurementGroup, final Iec60870Device device,
            final ResponseMetadata responseMetadata, final String errorPrefix) {

        this.findLightSensorStatusAndThen(measurementGroup,
                lightSensorStatus -> this.sendLightSensorStatus(lightSensorStatus, device, responseMetadata),
                () -> LOGGER.error("{} Light sensor status information not found for device {}", errorPrefix,
                        device.getDeviceIdentification()));
    }

    private void sendLightSensorStatus(final LightSensorStatusDto lightSensorStatus, final Iec60870Device device,
            final ResponseMetadata responseMetadata) {

        final ResponseMetadata rm = new ResponseMetadata.Builder()
                .withCorrelationUid(responseMetadata.getCorrelationUid())
                .withDeviceIdentification(device.getDeviceIdentification())
                .withDomainInfo(responseMetadata.getDomainInfo())
                .withMessageType(MessageType.GET_LIGHT_SENSOR_STATUS.name())
                .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
                .build();

        this.lightMeasurementService.sendSensorStatus(lightSensorStatus, rm);
    }

    private void findLightSensorStatusAndThen(final MeasurementGroupDto measurementGroup,
            final Consumer<? super LightSensorStatusDto> actionWithLightSensorStatus,
            final Runnable actionWhenLightSensorStatusNotFound) {

        final Optional<LightSensorStatusDto> optionalLightSensorStatus = this.findLightSensorStatus(measurementGroup);
        optionalLightSensorStatus.ifPresent(actionWithLightSensorStatus);
        if (!optionalLightSensorStatus.isPresent()) {
            actionWhenLightSensorStatusNotFound.run();
        }
    }

    private Optional<LightSensorStatusDto> findLightSensorStatus(final MeasurementGroupDto measurementGroup) {
        return this.findSensorValue(measurementGroup).map(LightSensorStatusDto::new);
    }

    private void sendEvent(final MeasurementGroupDto measurementGroup, final Iec60870Device device,
            final ResponseMetadata responseMetadata, final String errorPrefix) {

        this.findEventNotificationAndThen(measurementGroup, device,
                eventNotification -> this.sendEvent(eventNotification, device, responseMetadata),
                () -> LOGGER.error("{} Event information not found for device {}", errorPrefix,
                        device.getDeviceIdentification()));
    }

    private void sendEvent(final EventNotificationDto eventNotification, final Iec60870Device device,
            final ResponseMetadata responseMetadata) {

        final ResponseMetadata rm = new ResponseMetadata.Builder()
                .withCorrelationUid(responseMetadata.getCorrelationUid())
                .withDeviceIdentification(device.getDeviceIdentification())
                .withDomainInfo(responseMetadata.getDomainInfo())
                .withMessageType(MessageType.EVENT_NOTIFICATION.name())
                .withOrganisationIdentification(responseMetadata.getOrganisationIdentification())
                .build();

        this.lightMeasurementService.sendEventNotification(eventNotification, rm);
    }

    private void findEventNotificationAndThen(final MeasurementGroupDto measurementGroup, final Iec60870Device device,
            final Consumer<? super EventNotificationDto> actionWithEventNotification,
            final Runnable actionWhenEventNotificationNotFound) {

        final Optional<EventNotificationDto> optionalEventNotification = this.findEventNotification(measurementGroup,
                device);
        optionalEventNotification.ifPresent(actionWithEventNotification);
        if (!optionalEventNotification.isPresent()) {
            actionWhenEventNotificationNotFound.run();
        }
    }

    private EventTypeDto determineLightSensorEventType(final boolean on) {
        return on ? EventTypeDto.LIGHT_SENSOR_REPORTS_DARK : EventTypeDto.LIGHT_SENSOR_REPORTS_LIGHT;
    }

    private Optional<EventNotificationDto> findEventNotification(final MeasurementGroupDto measurementGroup,
            final Iec60870Device device) {

        final String deviceUid = device.getDeviceIdentification();
        final int index = device.getInformationObjectAddress();

        final EventTypeDto eventType = this.findSensorValue(measurementGroup)
                .map(this::determineLightSensorEventType)
                .orElse(null);
        if (eventType == null) {
            LOGGER.warn("Unable to determine event type for event notification for device {}",
                    device.getDeviceIdentification());
        }

        final DateTime dateTime = this.findTimeValue(measurementGroup).orElse(null);
        if (dateTime == null) {
            LOGGER.warn("Unable to determine the time for event notification for device {}",
                    device.getDeviceIdentification());
        }

        final EventNotificationDto eventNotification;
        if (eventType == null || dateTime == null) {
            eventNotification = null;
        } else {
            eventNotification = new EventNotificationDto(deviceUid, dateTime, eventType, "Spontaneous report", index);
        }
        return Optional.ofNullable(eventNotification);
    }

    private Optional<Boolean> findSensorValue(final MeasurementGroupDto measurementGroup) {
        return measurementGroup.getMeasurements()
                .stream()
                .flatMap(measurement -> measurement.getMeasurementElements().stream())
                .filter(element -> element instanceof BitmaskMeasurementElementDto)
                .map(element -> ((BitmaskMeasurementElementDto) element).getValue() == 1)
                .findFirst();
    }

    private Optional<DateTime> findTimeValue(final MeasurementGroupDto measurementGroup) {
        return measurementGroup.getMeasurements()
                .stream()
                .flatMap(measurement -> measurement.getMeasurementElements().stream())
                .filter(element -> element instanceof TimestampMeasurementElementDto)
                .map(element -> ((TimestampMeasurementElementDto) element).getValue())
                .map(DateTime::new)
                .findFirst();
    }
}
