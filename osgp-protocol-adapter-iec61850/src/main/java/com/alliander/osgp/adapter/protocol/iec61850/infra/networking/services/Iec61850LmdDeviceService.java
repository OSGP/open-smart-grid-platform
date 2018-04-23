/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceMessageStatus;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceRequest;
import com.alliander.osgp.adapter.protocol.iec61850.device.DeviceResponseHandler;
import com.alliander.osgp.adapter.protocol.iec61850.device.lmd.LmdDeviceService;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.EmptyDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.device.ssld.responses.GetStatusDeviceResponse;
import com.alliander.osgp.adapter.protocol.iec61850.domain.valueobjects.DeviceConnectionParameters;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.ConnectionFailureException;
import com.alliander.osgp.adapter.protocol.iec61850.exceptions.NodeException;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.Iec61850Client;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.DeviceConnection;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.IED;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.helper.LogicalDevice;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850EnableReportingCommand;
import com.alliander.osgp.adapter.protocol.iec61850.infra.networking.services.commands.Iec61850GetLightSensorStatusCommand;
import com.alliander.osgp.core.db.api.iec61850.application.services.LmdDataService;
import com.alliander.osgp.core.db.api.iec61850.entities.LightMeasurementDevice;
import com.alliander.osgp.dto.valueobjects.DeviceStatusDto;

@Component
public class Iec61850LmdDeviceService implements LmdDeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec61850LmdDeviceService.class);

    @Autowired
    private Iec61850Client iec61850Client;

    @Autowired
    private Iec61850DeviceConnectionService iec61850DeviceConnectionService;

    @Autowired
    private LmdDataService lmdDataService;

    @Autowired
    private Boolean isBufferedReportingEnabled;

    @Override
    public void getStatus(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler)
            throws JMSException {
        DeviceConnection devCon = null;
        try {
            final DeviceConnection deviceConnection = this.connectToDevice(deviceRequest);
            devCon = deviceConnection;

            final LightMeasurementDevice lmd = this.lmdDataService.findDevice(deviceRequest.getDeviceIdentification());

            LOGGER.info("Iec61850LmdDeviceService.getStatus() called for LMD: {}", lmd);

            final DeviceStatusDto deviceStatus = new Iec61850GetLightSensorStatusCommand()
                    .getStatusFromDevice(this.iec61850Client, deviceConnection, lmd);

            final GetStatusDeviceResponse deviceResponse = new GetStatusDeviceResponse(
                    deviceRequest.getOrganisationIdentification(), deviceRequest.getDeviceIdentification(),
                    deviceRequest.getCorrelationUid(), deviceStatus);

            deviceResponseHandler.handleResponse(deviceResponse);

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

    private DeviceConnection connectToDevice(final DeviceRequest deviceRequest) throws ConnectionFailureException {

        final DeviceConnectionParameters deviceConnectionParameters = DeviceConnectionParameters.newBuilder()
                .ipAddress(deviceRequest.getIpAddress()).deviceIdentification(deviceRequest.getDeviceIdentification())
                .ied(IED.ABB_RTU).serverName(IED.ABB_RTU.getDescription())
                .logicalDevice(LogicalDevice.LD0.getDescription()).build();

        return this.iec61850DeviceConnectionService.connect(deviceConnectionParameters,
                deviceRequest.getOrganisationIdentification());
    }

    // ========================
    // PRIVATE HELPER METHODS =
    // ========================

    private EmptyDeviceResponse createDefaultResponse(final DeviceRequest deviceRequest,
            final DeviceMessageStatus deviceMessageStatus) {
        return new EmptyDeviceResponse(deviceRequest.getOrganisationIdentification(),
                deviceRequest.getDeviceIdentification(), deviceRequest.getCorrelationUid(), deviceMessageStatus);
    }

    private void handleConnectionFailureException(final DeviceRequest deviceRequest,
            final DeviceResponseHandler deviceResponseHandler,
            final ConnectionFailureException connectionFailureException) throws JMSException {
        LOGGER.error("Could not connect to device", connectionFailureException);
        final EmptyDeviceResponse deviceResponse = this.createDefaultResponse(deviceRequest,
                DeviceMessageStatus.FAILURE);
        deviceResponseHandler.handleConnectionFailure(connectionFailureException, deviceResponse);
    }

    private void handleException(final DeviceRequest deviceRequest, final DeviceResponseHandler deviceResponseHandler,
            final Exception exception) {
        LOGGER.error("Unexpected exception", exception);
        final EmptyDeviceResponse deviceResponse = this.createDefaultResponse(deviceRequest,
                DeviceMessageStatus.FAILURE);
        deviceResponseHandler.handleException(exception, deviceResponse);
    }

    private void enableReporting(final DeviceConnection deviceConnection, final DeviceRequest deviceRequest)
            throws NodeException {
        LOGGER.info("Trying to enable reporting for device: {}", deviceRequest.getDeviceIdentification());
        if (this.isBufferedReportingEnabled) {
            new Iec61850EnableReportingCommand().enableBufferedReportingOnLightMeasurementDevice(this.iec61850Client,
                    deviceConnection);
        } else {
            new Iec61850EnableReportingCommand().enableUnbufferedReportingOnLightMeasurementDevice(this.iec61850Client,
                    deviceConnection);
        }
    }
}
