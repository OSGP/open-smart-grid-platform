/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.services;

import javax.jms.JMSException;

import org.openmuc.j60870.CauseOfTransmission;
import org.openmuc.j60870.Connection;
import org.openmuc.j60870.ConnectionEventListener;
import org.openmuc.j60870.IeQualifierOfInterrogation;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceMessageStatus;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.DeviceResponseHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.responses.EmptyDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec60870.device.responses.GetHealthStatusDeviceResponse;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceConnectionParameters;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.messaging.GetHealthStatusListener;
import org.opensmartgridplatform.adapter.protocol.iec60870.infra.networking.helper.DeviceConnection;
import org.opensmartgridplatform.dto.da.GetHealthStatusResponseDto;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870DeviceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Iec60870DeviceService.class);
    private static final String HEALTH_STATUS_OK = "OK";

    @Autowired
    private Iec60870DeviceConnectionService iec60870DeviceConnectionService;

    @Autowired
    private Iec60870DeviceRepository deviceRepository;

    public void getHealthStatus(final MessageMetadata messageMetadata,
            final DeviceResponseHandler deviceResponseHandler) throws JMSException {

        LOGGER.info("getHealthStatus for IEC 60870-5-104 device {}", messageMetadata.getDeviceIdentification());

        try {
            final ConnectionEventListener asduListener = new GetHealthStatusListener();
            final DeviceConnection deviceConnection = this.connectToDevice(messageMetadata, asduListener);

            final Connection connection = deviceConnection.getConnection();
            final int commonAddress = deviceConnection.getDeviceConnectionParameters().getCommonAddress();
            connection.interrogation(commonAddress, CauseOfTransmission.ACTIVATION, new IeQualifierOfInterrogation(20));
            Thread.sleep(2000);

            final GetHealthStatusResponseDto response = new GetHealthStatusResponseDto(HEALTH_STATUS_OK);
            final GetHealthStatusDeviceResponse deviceResponse = new GetHealthStatusDeviceResponse(messageMetadata,
                    response);

            deviceResponseHandler.handleResponse(deviceResponse);
        } catch (final ConnectionFailureException se) {
            this.handleConnectionFailureException(messageMetadata, deviceResponseHandler, se);
        } catch (final Exception e) {
            this.handleException(messageMetadata, deviceResponseHandler, e);
        }

    }

    // =====================================
    // PUBLIC DEVICE COMMUNICATION METHODS =
    // =====================================

    public DeviceConnection connectToDevice(final MessageMetadata messageMetadata,
            final ConnectionEventListener asduListener) throws ConnectionFailureException {

        final String deviceIdentification = messageMetadata.getDeviceIdentification();
        final Iec60870Device device = this.deviceRepository.findByDeviceIdentification(deviceIdentification);

        final DeviceConnectionParameters deviceConnectionParameters = DeviceConnectionParameters.newBuilder()
                .ipAddress(messageMetadata.getIpAddress()).deviceIdentification(deviceIdentification)
                .commonAddress(device.getCommonAddress()).port(device.getPort()).build();

        return this.iec60870DeviceConnectionService.connect(deviceConnectionParameters, asduListener);
    }

    // ========================
    // PRIVATE HELPER METHODS =
    // ========================

    private EmptyDeviceResponse createDefaultResponse(final MessageMetadata messageMetadata,
            final DeviceMessageStatus deviceMessageStatus) {
        return new EmptyDeviceResponse(messageMetadata.getOrganisationIdentification(),
                messageMetadata.getDeviceIdentification(), messageMetadata.getCorrelationUid(),
                messageMetadata.getMessagePriority(), deviceMessageStatus);
    }

    private void handleConnectionFailureException(final MessageMetadata messageMetadata,
            final DeviceResponseHandler deviceResponseHandler,
            final ConnectionFailureException connectionFailureException) throws JMSException {
        LOGGER.error("Could not connect to device", connectionFailureException);
        final EmptyDeviceResponse deviceResponse = this.createDefaultResponse(messageMetadata,
                DeviceMessageStatus.FAILURE);
        deviceResponseHandler.handleConnectionFailure(connectionFailureException, deviceResponse);
    }

    private void handleException(final MessageMetadata messageMetadata,
            final DeviceResponseHandler deviceResponseHandler, final Exception exception) {
        LOGGER.error("Unexpected exception", exception);
        final EmptyDeviceResponse deviceResponse = this.createDefaultResponse(messageMetadata,
                DeviceMessageStatus.FAILURE);
        deviceResponseHandler.handleException(exception, deviceResponse);
    }

}
