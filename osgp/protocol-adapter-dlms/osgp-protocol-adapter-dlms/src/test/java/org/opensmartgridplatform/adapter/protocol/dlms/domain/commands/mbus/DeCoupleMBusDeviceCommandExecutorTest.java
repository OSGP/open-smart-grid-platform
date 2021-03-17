/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.MethodResultCode;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DeCoupleMbusDeviceResponseDto;

@ExtendWith(MockitoExtension.class)
public class DeCoupleMBusDeviceCommandExecutorTest {

    @Mock
    private DeviceChannelsHelper deviceChannelsHelper;

    @Mock
    private DlmsConnectionManager conn;

    @Mock
    private DlmsDevice device;

    @Mock
    private DeCoupleMbusDeviceDto decoupleMbusDto;

    @InjectMocks
    private DeCoupleMBusDeviceCommandExecutor commandExecutor = new DeCoupleMBusDeviceCommandExecutor();

    @Test
    public void test() throws ProtocolAdapterException {

        final short channel = (short) 1;
        final ChannelElementValuesDto channelElementValuesDto = mock(ChannelElementValuesDto.class);

        when(this.deviceChannelsHelper.getObisCode(channel)).thenReturn(new ObisCode("0.1.24.1.0.255"));
        when(this.decoupleMbusDto.getChannel()).thenReturn(channel);
        when(this.deviceChannelsHelper.deinstallSlave(eq(this.conn), eq(this.device), any(Short.class),
                any(CosemObjectAccessor.class))).thenReturn(MethodResultCode.SUCCESS);
        when(this.deviceChannelsHelper.makeChannelElementValues(eq(channel), anyList()))
                .thenReturn(channelElementValuesDto);

        final DeCoupleMbusDeviceResponseDto responseDto = this.commandExecutor.execute(this.conn, this.device,
                this.decoupleMbusDto);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getChannelElementValues()).isEqualTo(channelElementValuesDto);

        verify(this.deviceChannelsHelper, times(1)).getMBusClientAttributeValues(eq(this.conn), eq(this.device),
                any(Short.class));
        verify(this.deviceChannelsHelper, times(1)).makeChannelElementValues(eq(channel), any());
        verify(this.deviceChannelsHelper, times(1)).deinstallSlave(eq(this.conn), eq(this.device), any(Short.class),
                any(CosemObjectAccessor.class));
        verify(this.deviceChannelsHelper, times(1)).resetMBusClientAttributeValues(eq(this.conn), any(Short.class),
                any(String.class));

    }

}
