/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;
import java.util.Date;
import java.util.Random;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequest;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ProtocolResponseMessage;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private static final Random generator = new Random();

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;

    @Autowired
    private ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;

    // === REQUEST PERIODIC METER DATA ===

    public void requestPeriodicMeterReads(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final PeriodicMeterReadsRequest periodicMeterReadsRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestPeriodicMeterReads called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        ClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(deviceIdentification);

            conn = this.dlmsConnectionFactory.getConnection(device);

            final PeriodicMeterReadsContainer periodicMeterReadsContainer = this.getPeriodicMeterReadsCommandExecutor
                    .execute(conn, periodicMeterReadsRequest);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender,
                    periodicMeterReadsContainer);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestPeriodicMeterReads", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        } finally {
            if (conn != null && conn.isConnected()) {
                conn.close();
            }
        }
    }

    private OsgpException ensureOsgpException(final Exception e) {

        if (e instanceof OsgpException) {
            return (OsgpException) e;
        }

        return new TechnicalException(ComponentType.PROTOCOL_DLMS,
                "Unexpected exception while handling protocol request/response message", e);
    }

    private void sendResponseMessage(final String domain, final String domainVersion, final String messageType,
            final String correlationUid, final String organisationIdentification, final String deviceIdentification,
            final ResponseMessageResultType result, final OsgpException osgpException,
            final DeviceResponseMessageSender responseMessageSender, final Serializable responseObject) {

        final ProtocolResponseMessage responseMessage = new ProtocolResponseMessage(domain, domainVersion, messageType,
                correlationUid, organisationIdentification, deviceIdentification, result, osgpException, responseObject);

        responseMessageSender.send(responseMessage);
    }

    public void requestActualMeterReads(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ActualMeterReadsRequest actualMeterReadsRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestActualMeterReads called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        try {
            // Mock a return value for actual meter reads.
            final ActualMeterReads actualMeterReads = new ActualMeterReads(new Date(), this.getRandomPositive(),
                    this.getRandomPositive(), this.getRandomPositive(), this.getRandomPositive());

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender, actualMeterReads);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestActualMeterReads", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        }
    }

    public void requestReadAlarmRegister(final String organisationIdentification, final String deviceIdentification,
            final String correlationUid, final ReadAlarmRegisterRequest readAlarmRegisterRequest,
            final DeviceResponseMessageSender responseMessageSender, final String domain, final String domainVersion,
            final String messageType) {

        LOGGER.info("requestActualMeterReads called for device: {} for organisation: {}", deviceIdentification,
                organisationIdentification);

        ClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(deviceIdentification);

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AlarmNotifications alarmNotifications = this.readAlarmRegisterCommandExecutor.execute(conn,
                    readAlarmRegisterRequest);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.OK, null, responseMessageSender, alarmNotifications);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestReadAlarmRegister", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(domain, domainVersion, messageType, correlationUid, organisationIdentification,
                    deviceIdentification, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        } finally {
            if (conn != null && conn.isConnected()) {
                conn.close();
            }
        }
    }

    private long getRandomPositive() {
        long randomLong = generator.nextLong();

        // if the random long returns Long.MIN_VALUE, the absolute of that is
        // not a long.
        if (randomLong == Long.MIN_VALUE) {
            randomLong += 1;
        }
        return Math.abs(randomLong);
    }
}
