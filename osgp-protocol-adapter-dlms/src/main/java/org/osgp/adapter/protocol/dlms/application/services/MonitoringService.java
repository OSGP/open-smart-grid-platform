/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.adapter.protocol.dlms.application.services;

import java.io.Serializable;

import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetActualMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetPeriodicMeterReadsGasCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.GetProfileGenericDataCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.commands.ReadAlarmRegisterCommandExecutor;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.factories.DlmsConnectionHolder;
import org.osgp.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;

@Service(value = "dlmsDeviceMonitoringService")
public class MonitoringService {

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

    @Autowired
    private GetProfileGenericDataCommandExecutor getProfileGenericDataCommandExecutor;

    // === REQUEST PERIODIC METER DATA ===

    public Serializable requestPeriodicMeterReads(final DlmsConnectionHolder conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDto periodicMeterReadsQuery) throws ProtocolAdapterException {

        Serializable response = null;
        if (periodicMeterReadsQuery.isMbusQuery()) {
            response = this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
        } else {
            response = this.getPeriodicMeterReadsCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
        }

        return response;

    }

    public Serializable requestActualMeterReads(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ActualMeterReadsQueryDto actualMeterReadsRequest) throws ProtocolAdapterException {

        Serializable response = null;
        if (actualMeterReadsRequest.isMbusQuery()) {
            response = this.actualMeterReadsGasCommandExecutor.execute(conn, device, actualMeterReadsRequest);
        } else {
            response = this.actualMeterReadsCommandExecutor.execute(conn, device, actualMeterReadsRequest);
        }

        return response;
    }

    public AlarmRegisterResponseDto requestReadAlarmRegister(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ReadAlarmRegisterRequestDto readAlarmRegisterRequest) throws ProtocolAdapterException {

        return this.readAlarmRegisterCommandExecutor.execute(conn, device, readAlarmRegisterRequest);
    }

    public Serializable requestProfileGenericData(final DlmsConnectionHolder conn, final DlmsDevice device,
            final ProfileGenericDataRequestDto profileGenericDataRequest) throws ProtocolAdapterException {

        return this.getProfileGenericDataCommandExecutor.execute(conn, device, profileGenericDataRequest);
    }

}
