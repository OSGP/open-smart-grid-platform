/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.services;

import java.io.Serializable;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.ClearAlarmRegisterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm.ReadAlarmRegisterCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualMeterReadsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetActualMeterReadsGasCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.misc.GetPowerQualityProfileCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.periodicmeterreads.GetPeriodicMeterReadsGasCommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ActualMeterReadsQueryDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmRegisterResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private GetPowerQualityProfileCommandExecutor getPowerQualityProfileCommandExecutor;

    @Autowired
    private ClearAlarmRegisterCommandExecutor clearAlarmRegisterCommandExecutor;

    // === REQUEST PERIODIC METER DATA ===

    public Serializable requestPeriodicMeterReads(final DlmsConnectionManager conn, final DlmsDevice device,
            final PeriodicMeterReadsRequestDto periodicMeterReadsQuery) throws ProtocolAdapterException {

        final Serializable response;
        if (periodicMeterReadsQuery.isMbusQuery()) {
            response = this.getPeriodicMeterReadsGasCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
        } else {
            response = this.getPeriodicMeterReadsCommandExecutor.execute(conn, device, periodicMeterReadsQuery);
        }

        return response;

    }

    public Serializable requestActualMeterReads(final DlmsConnectionManager conn, final DlmsDevice device,
            final ActualMeterReadsQueryDto actualMeterReadsRequest) throws ProtocolAdapterException {

        final Serializable response;
        if (actualMeterReadsRequest.isMbusQuery()) {
            response = this.actualMeterReadsGasCommandExecutor.execute(conn, device, actualMeterReadsRequest);
        } else {
            response = this.actualMeterReadsCommandExecutor.execute(conn, device, actualMeterReadsRequest);
        }

        return response;
    }

    public AlarmRegisterResponseDto requestReadAlarmRegister(final DlmsConnectionManager conn, final DlmsDevice device,
            final ReadAlarmRegisterRequestDto readAlarmRegisterRequest) throws ProtocolAdapterException {

        return this.readAlarmRegisterCommandExecutor.execute(conn, device, readAlarmRegisterRequest);
    }

    public Serializable requestPowerQualityProfile(final DlmsConnectionManager conn, final DlmsDevice device,
            final GetPowerQualityProfileRequestDataDto powerQualityProfileRequestDataDto)
            throws ProtocolAdapterException {

        return this.getPowerQualityProfileCommandExecutor.execute(conn, device, powerQualityProfileRequestDataDto);
    }

    public void setClearAlarmRegister(final DlmsConnectionManager conn, final DlmsDevice device,
            final ClearAlarmRegisterRequestDto clearAlarmRegisterRequestDto) throws ProtocolAdapterException {

        this.clearAlarmRegisterCommandExecutor.execute(conn, device, clearAlarmRegisterRequestDto);
    }

}
