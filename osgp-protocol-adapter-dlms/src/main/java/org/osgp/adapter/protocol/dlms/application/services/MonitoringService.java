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

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.infra.messaging.DeviceResponseMessageSender;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;
import com.alliander.osgp.shared.exceptionhandling.TechnicalException;
import com.alliander.osgp.shared.infra.jms.ResponseMessageResultType;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService extends DlmsApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private static final Random generator = new Random();

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;

    @Autowired
    private GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor;

    @Autowired
    private GetActualMeterReadsGasCommandExecutor actualMeterReadsGasCommandExecutor;

    @Autowired
    private ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;

    // === REQUEST PERIODIC METER DATA ===

    public void requestPeriodicMeterReads(final DlmsDeviceMessageMetadata messageMetadata,
            final PeriodicMeterReadsQuery periodicMeterReadsQuery,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestPeriodicMeterReads");

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            Serializable response = null;
            if (periodicMeterReadsQuery.isGas()) {
                response = this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, periodicMeterReadsQuery);
            } else {
                response = this.getPeriodicMeterReadsCommandExecutor.execute(conn, periodicMeterReadsQuery);
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    response);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestPeriodicMeterReads", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender,
                    periodicMeterReadsQuery);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public void requestActualMeterReads(final DlmsDeviceMessageMetadata messageMetadata,
            final ActualMeterReadsQuery actualMeterReadsRequest, final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestActualMeterReads");

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            Serializable response = null;
            if (actualMeterReadsRequest.isGas()) {
                response = this.actualMeterReadsGasCommandExecutor.execute(conn, actualMeterReadsRequest);
            } else {
                // mock for now
                response = new MeterReads(new Date(), this.getRandomPositive(), this.getRandomPositive(),
                        this.getRandomPositive(), this.getRandomPositive());
            }

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    response);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestPeriodicMeterReads", e);
            final OsgpException ex = this.ensureOsgpException(e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }

    }

    public void requestReadAlarmRegister(final DlmsDeviceMessageMetadata messageMetadata,
            final ReadAlarmRegisterRequest readAlarmRegisterRequest,
            final DeviceResponseMessageSender responseMessageSender) {

        this.logStart(LOGGER, messageMetadata, "requestReadAlarmRegister");

        LnClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService
                    .findDlmsDevice(messageMetadata.getDeviceIdentification());

            conn = this.dlmsConnectionFactory.getConnection(device);

            final AlarmRegister alarmRegister = this.readAlarmRegisterCommandExecutor.execute(conn,
                    readAlarmRegisterRequest);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.OK, null, responseMessageSender,
                    alarmRegister);

        } catch (final Exception e) {
            LOGGER.error("Unexpected exception during requestReadAlarmRegister", e);
            final TechnicalException ex = new TechnicalException(ComponentType.UNKNOWN,
                    "Unexpected exception while retrieving response message", e);

            this.sendResponseMessage(messageMetadata, ResponseMessageResultType.NOT_OK, ex, responseMessageSender, null);
        } finally {
            if (conn != null) {
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
