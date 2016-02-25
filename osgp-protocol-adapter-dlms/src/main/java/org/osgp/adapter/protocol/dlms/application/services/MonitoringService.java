/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;

import org.openmuc.jdlms.LnClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.osgp.adapter.protocol.dlms.infra.messaging.DlmsDeviceMessageMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private DlmsConnectionFactory dlmsConnectionFactory;

    @Autowired
    private GetPeriodicMeterReadsCommandExecutor getPeriodicMeterReadsCommandExecutor;

    @Autowired
    private GetPeriodicMeterReadsGasCommandExecutor getPeriodicMeterReadsGasCommandExecutor;

    @Autowired
    private GetActualMeterReadsCommandExecutor actualMeterReadsCommandExecutor;

    @Autowired
    private GetActualMeterReadsGasCommandExecutor actualMeterReadsGasCommandExecutor;

    @Autowired
    private ReadAlarmRegisterCommandExecutor readAlarmRegisterCommandExecutor;

    // === REQUEST PERIODIC METER DATA ===

    public Serializable requestPeriodicMeterReads(final DlmsDeviceMessageMetadata messageMetadata,
            final PeriodicMeterReadsQuery periodicMeterReadsQuery) throws OsgpException, ProtocolAdapterException {

        LnClientConnection conn = null;
        try {

            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            Serializable response = null;
            if (periodicMeterReadsQuery.isGas()) {
                response = this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
            } else {
                response = this.getPeriodicMeterReadsCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
            }

            return response;

        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public Serializable requestActualMeterReads(final DlmsDeviceMessageMetadata messageMetadata,
            final ActualMeterReadsQuery actualMeterReadsRequest) throws OsgpException, ProtocolAdapterException {

        LnClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);
            conn = this.dlmsConnectionFactory.getConnection(device);

            Serializable response = null;
            if (actualMeterReadsRequest.isGas()) {
                response = this.actualMeterReadsGasCommandExecutor.execute(conn, device, actualMeterReadsRequest);
            } else {
                response = this.actualMeterReadsCommandExecutor.execute(conn, device, actualMeterReadsRequest);
            }

            return response;
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }

    public AlarmRegister requestReadAlarmRegister(final DlmsDeviceMessageMetadata messageMetadata,
            final ReadAlarmRegisterRequest readAlarmRegisterRequest) throws OsgpException, ProtocolAdapterException {

        LnClientConnection conn = null;
        try {
            final DlmsDevice device = this.domainHelperService.findDlmsDevice(messageMetadata);

            conn = this.dlmsConnectionFactory.getConnection(device);

            return this.readAlarmRegisterCommandExecutor.execute(conn, device, readAlarmRegisterRequest);
        } finally {
            if (conn != null) {
                conn.close();
            }
        }
    }
}
