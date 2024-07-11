// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.MethodResultCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.CosemObjectAccessor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.DecoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
public class DecoupleMBusDeviceCommandExecutorTest {

  @Mock private DeviceChannelsHelper deviceChannelsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice device;

  @Mock private DecoupleMbusDeviceDto decoupleMbusDto;

  @InjectMocks private DecoupleMBusDeviceCommandExecutor commandExecutor;

  @Mock private ChannelElementValuesDto channelElementValuesDto;

  private static final short CHANNEL = (short) 1;

  private final MessageMetadata messageMetadata =
      MessageMetadata.newBuilder().withCorrelationUid("123456").build();

  @BeforeEach
  void setup() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);
    ReflectionTestUtils.setField(
        this.commandExecutor, "objectConfigServiceHelper", objectConfigServiceHelper);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      mode = Mode.EXCLUDE,
      names = {"OTHER_PROTOCOL"})
  void testHappyFlow(final Protocol protocol) throws ProtocolAdapterException {
    this.prepareWhen(protocol);
    when(this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, CHANNEL))
        .thenReturn(this.channelElementValuesDto);
    this.executeAndAssertResponse(this.messageMetadata, this.channelElementValuesDto);
  }

  @Test
  void testInvalidIdentificationNumber() throws ProtocolAdapterException {
    this.prepareWhen(Protocol.SMR_5_0_0);
    final InvalidIdentificationNumberException exception =
        new InvalidIdentificationNumberException("exception", this.channelElementValuesDto);
    when(this.deviceChannelsHelper.getChannelElementValues(this.conn, this.device, CHANNEL))
        .thenThrow(exception);
    this.executeAndAssertResponse(this.messageMetadata, this.channelElementValuesDto);
  }

  private void prepareWhen(final Protocol protocol) throws ProtocolAdapterException {
    when(this.device.getProtocolName()).thenReturn(protocol.getName());
    when(this.device.getProtocolVersion()).thenReturn(protocol.getVersion());
    when(this.decoupleMbusDto.getChannel()).thenReturn(CHANNEL);
    when(this.deviceChannelsHelper.deinstallSlave(
            eq(this.conn), eq(this.device), any(Short.class), any(CosemObjectAccessor.class)))
        .thenReturn(MethodResultCode.SUCCESS);
  }

  private void executeAndAssertResponse(
      final MessageMetadata messageMetadata, final ChannelElementValuesDto channelElementValuesDto)
      throws ProtocolAdapterException {
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
