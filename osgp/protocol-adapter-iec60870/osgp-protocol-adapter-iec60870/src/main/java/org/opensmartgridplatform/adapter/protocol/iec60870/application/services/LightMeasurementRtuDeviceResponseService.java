/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.application.services;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.ResponseMetadata;
import org.opensmartgridplatform.dto.da.measurements.MeasurementReportDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class LightMeasurementRtuDeviceResponseService extends AbstractDeviceResponseService {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(LightMeasurementRtuDeviceResponseService.class);

  private static final String ERROR_PREFIX =
      "Error while processing measurement report for light measurement RTU: ";

  private static final DeviceType DEVICE_TYPE = DeviceType.LIGHT_MEASUREMENT_RTU;

  @Autowired private Iec60870DeviceRepository iec60870DeviceRepository;

  @Autowired private LightSensorDeviceResponseService lightMeasurementDeviceResponseService;

  public LightMeasurementRtuDeviceResponseService() {
    super(DEVICE_TYPE);
  }

  @Override
  public void process(
      final MeasurementReportDto measurementReport, final ResponseMetadata responseMetadata) {
    final String rtuDeviceIdentification = responseMetadata.getDeviceIdentification();
    LOGGER.info(
        "Received measurement report {} for light measurement RTU {}.",
        measurementReport,
        rtuDeviceIdentification);

    this.findLightSensorsAndThen(
        rtuDeviceIdentification,
        device ->
            this.processMeasurementReportForDevice(
                measurementReport, responseMetadata, device, rtuDeviceIdentification));
  }

  private void processMeasurementReportForDevice(
      final MeasurementReportDto measurementReport,
      final ResponseMetadata responseMetadata,
      final Iec60870Device device,
      final String rtuDeviceIdentification) {

    LOGGER.info(
        "Processing measurement report for light sensor {} on RTU {}",
        device.getDeviceIdentification(),
        rtuDeviceIdentification);
    this.lightMeasurementDeviceResponseService.sendLightSensorStatusResponse(
        measurementReport, device, responseMetadata, ERROR_PREFIX);
  }

  @Override
  public void processEvent(
      final MeasurementReportDto measurementReport, final ResponseMetadata responseMetadata) {
    final String rtuDeviceIdentification = responseMetadata.getDeviceIdentification();
    LOGGER.info(
        "Received event {} for light measurement RTU {}.",
        measurementReport,
        rtuDeviceIdentification);

    this.findLightSensorsAndThen(
        rtuDeviceIdentification,
        device ->
            this.processEventForDevice(
                measurementReport, responseMetadata, device, rtuDeviceIdentification));
  }

  private void processEventForDevice(
      final MeasurementReportDto measurementReport,
      final ResponseMetadata responseMetadata,
      final Iec60870Device device,
      final String rtuDeviceIdentification) {

    LOGGER.info(
        "Processing event for light sensor {} on RTU {}",
        device.getDeviceIdentification(),
        rtuDeviceIdentification);
    this.lightMeasurementDeviceResponseService.sendEvent(
        measurementReport, device, responseMetadata, ERROR_PREFIX);
  }

  private void findLightSensorsAndThen(
      final String rtuDeviceIdentification,
      final Consumer<? super Iec60870Device> actionPerDevice) {

    final List<Iec60870Device> lightMeasurementDevices =
        this.iec60870DeviceRepository.findByGatewayDeviceIdentification(rtuDeviceIdentification)
            .stream()
            .filter(device -> DeviceType.LIGHT_SENSOR == device.getDeviceType())
            .collect(Collectors.toList());

    if (CollectionUtils.isEmpty(lightMeasurementDevices)) {
      LOGGER.warn("No light sensors found for light measurement RTU {}", rtuDeviceIdentification);
      return;
    }

    lightMeasurementDevices.forEach(actionPerDevice);
  }
}
