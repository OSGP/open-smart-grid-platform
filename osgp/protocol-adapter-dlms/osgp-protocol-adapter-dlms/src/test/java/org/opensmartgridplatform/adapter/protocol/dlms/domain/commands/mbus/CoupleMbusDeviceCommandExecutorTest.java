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

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceResponseDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class CoupleMbusDeviceCommandExecutorTest {

  @InjectMocks private CoupleMBusDeviceCommandExecutor commandExecutor;

  private short channel;
  private Short primaryAddress;
  private String mbusDeviceIdentification;
  private String manufacturerIdentification;
  private short version;
  private short deviceTypeIdentification;
  private String identificationNumber;
  private List<ChannelElementValuesDto> candidateChannelElementValues;
  private MessageMetadata messageMetadata;

  @Mock private DeviceChannelsHelper deviceChannelsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice device;

  @BeforeEach
  public void setUp() {
    this.channel = (short) 1;
    this.primaryAddress = 9;
    this.mbusDeviceIdentification = "G00009";
    this.manufacturerIdentification = "manufacturerIdentification";
    this.version = 123;
    this.deviceTypeIdentification = 456;
    this.identificationNumber = "identificationNumber";
    this.candidateChannelElementValues =
        Arrays.asList(
            new ChannelElementValuesDto(
                this.channel,
                this.primaryAddress,
                this.identificationNumber,
                this.manufacturerIdentification,
                this.version,
                this.deviceTypeIdentification));
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  }

  @Test
  public void testExactMatch() throws ProtocolAdapterException {

    final MbusChannelElementsDto mbusChannelElementsDto =
        new MbusChannelElementsDto(
            this.primaryAddress,
            this.mbusDeviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);
    final CoupleMbusDeviceRequestDataDto requestDataDto =
        new CoupleMbusDeviceRequestDataDto(
            this.mbusDeviceIdentification, false, mbusChannelElementsDto);

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(this.candidateChannelElementValues);

    final CoupleMbusDeviceResponseDto responseDto =
        this.commandExecutor.execute(this.conn, this.device, requestDataDto, this.messageMetadata);

    final ChannelElementValuesDto channelElementValuesDto = responseDto.getChannelElementValues();
    assertThat(channelElementValuesDto.getChannel()).isEqualTo(this.channel);
    assertThat(channelElementValuesDto.getDeviceTypeIdentification())
        .isEqualTo(this.deviceTypeIdentification);
    assertThat(channelElementValuesDto.getIdentificationNumber())
        .isEqualTo(this.identificationNumber);
    assertThat(channelElementValuesDto.getManufacturerIdentification())
        .isEqualTo(this.manufacturerIdentification);
    assertThat(channelElementValuesDto.getPrimaryAddress()).isEqualTo(this.primaryAddress);
    assertThat(channelElementValuesDto.getVersion()).isEqualTo(this.version);

    verify(this.deviceChannelsHelper, times(1))
        .findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class));
  }

  @Test
  public void testBestMatch() throws ProtocolAdapterException {

    final MbusChannelElementsDto mbusChannelElementsDto =
        new MbusChannelElementsDto(
            (short) 8,
            this.mbusDeviceIdentification,
            this.identificationNumber,
            this.manufacturerIdentification,
            this.version,
            this.deviceTypeIdentification);
    final CoupleMbusDeviceRequestDataDto requestDataDto =
        new CoupleMbusDeviceRequestDataDto(
            this.mbusDeviceIdentification, false, mbusChannelElementsDto);

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(this.candidateChannelElementValues);

    final CoupleMbusDeviceResponseDto responseDto =
        this.commandExecutor.execute(this.conn, this.device, requestDataDto, this.messageMetadata);

    final ChannelElementValuesDto channelElementValuesDto = responseDto.getChannelElementValues();
    assertThat(channelElementValuesDto.getChannel()).isEqualTo(this.channel);
    assertThat(channelElementValuesDto.getDeviceTypeIdentification())
        .isEqualTo(this.deviceTypeIdentification);
    assertThat(channelElementValuesDto.getIdentificationNumber())
        .isEqualTo(this.identificationNumber);
    assertThat(channelElementValuesDto.getManufacturerIdentification())
        .isEqualTo(this.manufacturerIdentification);
    assertThat(channelElementValuesDto.getPrimaryAddress()).isEqualTo(this.primaryAddress);
    assertThat(channelElementValuesDto.getVersion()).isEqualTo(this.version);

    verify(this.deviceChannelsHelper, times(1))
        .findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class));
  }

  @Test
  public void testNoMatchAndNoEmptyChannel() throws ProtocolAdapterException {

    final MbusChannelElementsDto mbusChannelElementsDto =
        new MbusChannelElementsDto(
            (short) -1, "noMatch", "noMatch", "noMatch", (short) -1, (short) -1);
    final CoupleMbusDeviceRequestDataDto requestDataDto =
        new CoupleMbusDeviceRequestDataDto(
            this.mbusDeviceIdentification, false, mbusChannelElementsDto);

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(this.candidateChannelElementValues);

    final CoupleMbusDeviceResponseDto responseDto =
        this.commandExecutor.execute(this.conn, this.device, requestDataDto, this.messageMetadata);

    final ChannelElementValuesDto channelElementValuesDto = responseDto.getChannelElementValues();
    assertThat(channelElementValuesDto).isNull();

    verify(this.deviceChannelsHelper, times(1))
        .findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class));
  }

  @Test
  public void testNoMatchButEmptyChannel() throws ProtocolAdapterException {
    // Add empty channel to the list of candidate channels
    final ChannelElementValuesDto emptyChannel =
        new ChannelElementValuesDto((short) 2, (short) 0, "00000000", null, (short) 0, (short) 0);
    final List<ChannelElementValuesDto> candidateChannelElementValuesWithEmptyChannel =
        Arrays.asList(this.candidateChannelElementValues.get(0), emptyChannel);

    final MbusChannelElementsDto mbusChannelElementsDto =
        new MbusChannelElementsDto(
            (short) -1, "noMatch", "noMatch", "noMatch", (short) -1, (short) -1);
    final CoupleMbusDeviceRequestDataDto requestDataDto =
        new CoupleMbusDeviceRequestDataDto(
            this.mbusDeviceIdentification, false, mbusChannelElementsDto);

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(candidateChannelElementValuesWithEmptyChannel);
    when(this.deviceChannelsHelper.findEmptyChannel(candidateChannelElementValuesWithEmptyChannel))
        .thenReturn(emptyChannel);
    when(this.deviceChannelsHelper.writeUpdatedMbus(
            eq(this.conn),
            eq(this.device),
            eq(mbusChannelElementsDto),
            eq(emptyChannel.getChannel()),
            any(String.class)))
        .thenReturn(
            new ChannelElementValuesDto(
                emptyChannel.getChannel(),
                mbusChannelElementsDto.getPrimaryAddress(),
                mbusChannelElementsDto.getMbusIdentificationNumber(),
                mbusChannelElementsDto.getMbusManufacturerIdentification(),
                mbusChannelElementsDto.getMbusVersion(),
                mbusChannelElementsDto.getMbusDeviceTypeIdentification()));

    final CoupleMbusDeviceResponseDto responseDto =
        this.commandExecutor.execute(this.conn, this.device, requestDataDto, this.messageMetadata);

    final ChannelElementValuesDto channelElementValuesDto = responseDto.getChannelElementValues();
    assertThat(channelElementValuesDto.getChannel()).isEqualTo(emptyChannel.getChannel());
    assertThat(channelElementValuesDto.getDeviceTypeIdentification())
        .isEqualTo(requestDataDto.getMbusChannelElements().getMbusDeviceTypeIdentification());
    assertThat(channelElementValuesDto.getIdentificationNumber())
        .isEqualTo(requestDataDto.getMbusChannelElements().getMbusIdentificationNumber());
    assertThat(channelElementValuesDto.getManufacturerIdentification())
        .isEqualTo(requestDataDto.getMbusChannelElements().getMbusManufacturerIdentification());
    assertThat(channelElementValuesDto.getPrimaryAddress())
        .isEqualTo(requestDataDto.getMbusChannelElements().getPrimaryAddress());
    assertThat(channelElementValuesDto.getVersion())
        .isEqualTo(requestDataDto.getMbusChannelElements().getMbusVersion());

    verify(this.deviceChannelsHelper, times(1))
        .findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class));
  }
}
