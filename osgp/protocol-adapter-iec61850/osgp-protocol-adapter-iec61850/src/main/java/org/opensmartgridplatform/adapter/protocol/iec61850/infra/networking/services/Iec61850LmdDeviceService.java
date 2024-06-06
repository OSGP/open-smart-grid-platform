// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services;

import jakarta.jms.JMSException;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.lmd.GetLightSensorStatusResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.lmd.LmdDeviceService;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import org.opensmartgridplatform.adapter.protocol.iec61850.exceptions.NodeException;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.IED;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850EnableReportingCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850GetLightSensorStatusCommand;
import org.opensmartgridplatform.adapter.protocol.iec61850.services.DeviceMessageLoggingService;
import org.opensmartgridplatform.core.db.api.iec61850.application.services.LmdDataService;
import org.opensmartgridplatform.core.db.api.iec61850.entities.LightMeasurementDevice;
import org.opensmartgridplatform.dto.valueobjects.LightSensorStatusDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Iec61850LmdDeviceService implements LmdDeviceService {

  private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850LmdDeviceService.class);

  @Autowired private Iec61850Client iec61850Client;

  @Autowired private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

  @Autowired
  @Qualifier(value = "protocolIec61850DeviceMessageLoggingService")
  private DeviceMessageLoggingService deviceMessageLoggingService;

  @Autowired private LmdDataService lmdDataService;

  @Autowired private Boolean isBufferedReportingEnabled;

  @Override
  public void getLightSensorStatus(
      final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
      throws JMSException {
    DeviceConnection devCon = null;
    try {
      final DeviceConnection deviceConnection = this.connectToDevice(deviceRequest);
      devCon = deviceConnection;

      final LightMeasurementDevice lmd =
          this.lmdDataService.findDevice(deviceRequest.getDeviceIdentification());

      LOGGER.info("Iec61850LmdDeviceService.getLightSensorStatus() called for LMD: {}", lmd);

      final LightSensorStatusDto lightSensorStatus =
          new Iec61850GetLightSensorStatusCommand(this.deviceMessageLoggingService)
              .getStatusFromDevice(this.iec61850Client, deviceConnection, lmd);

      final GetLightSensorStatusResponse lightSensorStatusResponse =
          new GetLightSensorStatusResponse(deviceRequest, lightSensorStatus);

      deviceResponseHandler.handleResponse(lightSensorStatusResponse);

      this.enableReporting(deviceConnection, deviceRequest);
    } catch (final ConnectionFailureException se) {
      this.handleConnectionFailureException(deviceRequest, deviceResponseHandler, se);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    } catch (final Exception e) {
      this.handleException(deviceRequest, deviceResponseHandler, e);
      this.iec61850DeviceConnectionService.disconnect(devCon, deviceRequest);
    }
  }

  // ======================================
  // PRIVATE DEVICE COMMUNICATION METHODS =
  // ======================================

  private DeviceConnection connectToDevice(final DeviceRequest deviceRequest)
      throws ConnectionFailureException {

    final DeviceConnectionParameters deviceConnectionParameters =
        DeviceConnectionParameters.newBuilder()
            .networkAddress(deviceRequest.getNetworkAddress())
            .deviceIdentification(deviceRequest.getDeviceIdentification())
            .ied(IED.ABB_RTU)
            .serverName(IED.ABB_RTU.getDescription())
            .logicalDevice(LogicalDevice.LD0.getDescription())
            .build();

    return this.iec61850DeviceConnectionService.connect(
        deviceConnectionParameters, deviceRequest.getOrganisationIdentification());
  }

  // ========================
  // PRIVATE HELPER METHODS =
  // ========================

  private EmptyDeviceResponse createDefaultResponse(
      final DeviceRequest deviceRequest, final DeviceMessageStatus deviceMessageStatus) {
    return new EmptyDeviceResponse(deviceRequest, deviceMessageStatus);
  }

  private void handleConnectionFailureException(
      final DeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final ConnectionFailureException connectionFailureException)
      throws JMSException {
    LOGGER.error("Could not connect to device", connectionFailureException);
    final EmptyDeviceResponse deviceResponse =
        this.createDefaultResponse(deviceRequest, DeviceMessageStatus.FAILURE);
    deviceResponseHandler.handleConnectionFailure(connectionFailureException, deviceResponse);
  }

  private void handleException(
      final DeviceRequest deviceRequest,
      final DeviceResponseHandler deviceResponseHandler,
      final Exception exception) {
    LOGGER.error("Unexpected exception", exception);
    final EmptyDeviceResponse deviceResponse =
        this.createDefaultResponse(deviceRequest, DeviceMessageStatus.FAILURE);
    deviceResponseHandler.handleException(exception, deviceResponse);
  }

  private void enableReporting(
      final DeviceConnection deviceConnection, final DeviceRequest deviceRequest)
      throws NodeException {
    LOGGER.info(
        "Trying to enable reporting for device: {}", deviceRequest.getDeviceIdentification());
    if (this.isBufferedReportingEnabled) {
      new Iec61850EnableReportingCommand()
          .enableBufferedReportingOnLightMeasurementDevice(this.iec61850Client, deviceConnection);
    } else {
      new Iec61850EnableReportingCommand()
          .enableUnbufferedReportingOnLightMeasurementDevice(this.iec61850Client, deviceConnection);
    }
  }
}
