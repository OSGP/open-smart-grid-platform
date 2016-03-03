/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;

import org.openmuc.jdlms.ClientConnection;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionFactory;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequest;

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

    public Serializable requestPeriodicMeterReads(final ClientConnection conn, final DlmsDevice device,
            final PeriodicMeterReadsQuery periodicMeterReadsQuery) throws ProtocolAdapterException {

        Serializable response = null;
        if (periodicMeterReadsQuery.isGas()) {
            response = this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
        } else {
            response = this.getPeriodicMeterReadsCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
        }

        return response;

    }

    public Serializable requestActualMeterReads(final ClientConnection conn, final DlmsDevice device,
            final ActualMeterReadsQuery actualMeterReadsRequest) throws ProtocolAdapterException {

        Serializable response = null;
        if (actualMeterReadsRequest.isGas()) {
            response = this.actualMeterReadsGasCommandExecutor.execute(conn, device, actualMeterReadsRequest);
        } else {
            response = this.actualMeterReadsCommandExecutor.execute(conn, device, actualMeterReadsRequest);
        }

        return response;
    }

    public AlarmRegister requestReadAlarmRegister(final ClientConnection conn, final DlmsDevice device,
            final ReadAlarmRegisterRequest readAlarmRegisterRequest) throws ProtocolAdapterException {

        return this.readAlarmRegisterCommandExecutor.execute(conn, device, readAlarmRegisterRequest);
    }
}
