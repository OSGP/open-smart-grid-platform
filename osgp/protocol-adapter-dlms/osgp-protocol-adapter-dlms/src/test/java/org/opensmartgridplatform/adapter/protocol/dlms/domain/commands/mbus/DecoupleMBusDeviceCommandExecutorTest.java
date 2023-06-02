//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class DecoupleMBusDeviceCommandExecutorTest {

  @Mock private DeviceChannelsHelper deviceChannelsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice device;

  @Mock private DecoupleMbusDeviceDto decoupleMbusDto;

  @InjectMocks private DecoupleMBusDeviceCommandExecutor commandExecutor;

  @Test
  public void testHappyFlow() throws ProtocolAdapterException {

    final short channel = (short) 1;
    final ChannelElementValuesDto channelElementValuesDto = mock(ChannelElementValuesDto.class);
    final MessageMetadata messageMetadata =
        MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    when(this.deviceChannelsHelper.getObisCode(this.device, channel))
        .thenReturn(new ObisCode("0.1.24.1.0.255"));
    when(this.decoupleMbusDto.getChannel()).thenReturn(channel);
    when(this.deviceChannelsHelper.deinstallSlave(
            eq(this.conn), eq(this.device), any(Short.class), any(CosemObjectAccessor.class)))
        .thenReturn(MethodResultCode.SUCCESS);
    when(this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, channel))
        .thenReturn(channelElementValuesDto);

    final DecoupleMbusDeviceResponseDto responseDto =
        this.commandExecutor.execute(this.conn, this.device, this.decoupleMbusDto, messageMetadata);

    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getChannelElementValues()).isEqualTo(channelElementValuesDto);

    verify(this.deviceChannelsHelper, times(1))
        .getChannelElementValues(eq(this.conn), eq(this.device), any(Short.class));
    verify(this.deviceChannelsHelper, times(1))
        .deinstallSlave(
            eq(this.conn), eq(this.device), any(Short.class), any(CosemObjectAccessor.class));
    verify(this.deviceChannelsHelper, times(1))
        .resetMBusClientAttributeValues(
            eq(this.conn), eq(this.device), any(Short.class), any(String.class));
  }
}
