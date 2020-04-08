package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.monitoring;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetPowerQualityProfileRequestDataDto;

/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
@ExtendWith(MockitoExtension.class)
public class GetPowerQualityProfileCommandExecutorTest {

    @Mock
    private GetPowerQualityProfileNoSelectiveAccessHandler getPowerQualityProfileNoSelectiveAccessHandler;

    @Mock
    private GetPowerQualityProfileSelectiveAccessHandler getPowerQualityProfileSelectiveAccessHandler;

    @Mock
    private DlmsConnectionManager conn;

    @Mock
    GetPowerQualityProfileRequestDataDto getPowerQualityProfileRequestDataDto;

    @InjectMocks
    private GetPowerQualityProfileCommandExecutor executor;

    @Test
    public void executeWithSelectiveAccess() throws ProtocolAdapterException {

        DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setSelectiveAccessSupported(true);

        executor.execute(conn, dlmsDevice, getPowerQualityProfileRequestDataDto);

        verify(getPowerQualityProfileSelectiveAccessHandler)
                .handle(any(DlmsConnectionManager.class), any(DlmsDevice.class),
                        any(GetPowerQualityProfileRequestDataDto.class));

    }

    @Test
    public void executeWithoutSelectiveAccess() throws ProtocolAdapterException {

        DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setSelectiveAccessSupported(false);

        executor.execute(conn, dlmsDevice, getPowerQualityProfileRequestDataDto);

        verify(getPowerQualityProfileNoSelectiveAccessHandler)
                .handle(any(DlmsConnectionManager.class), any(DlmsDevice.class),
                        any(GetPowerQualityProfileRequestDataDto.class));

    }
}
