/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.MbusChannelElementsResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class CoupleMbusDeviceCommandExecutorTest {

  @InjectMocks
  private final CoupleMBusDeviceCommandExecutor commandExecutor =
      new CoupleMBusDeviceCommandExecutor();

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

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(this.candidateChannelElementValues);

    final MbusChannelElementsResponseDto responseDto =
        this.commandExecutor.execute(
            this.conn, this.device, mbusChannelElementsDto, this.messageMetadata);

    assertThat(responseDto.getChannel()).isEqualTo(this.channel);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getDeviceTypeIdentification())
        .isEqualTo(this.deviceTypeIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getIdentificationNumber())
        .isEqualTo(this.identificationNumber);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getManufacturerIdentification())
        .isEqualTo(this.manufacturerIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getPrimaryAddress())
        .isEqualTo(this.primaryAddress);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getVersion())
        .isEqualTo(this.version);

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

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(this.candidateChannelElementValues);

    final MbusChannelElementsResponseDto responseDto =
        this.commandExecutor.execute(
            this.conn, this.device, mbusChannelElementsDto, this.messageMetadata);

    assertThat(responseDto.getChannel()).isEqualTo(this.channel);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getDeviceTypeIdentification())
        .isEqualTo(this.deviceTypeIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getIdentificationNumber())
        .isEqualTo(this.identificationNumber);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getManufacturerIdentification())
        .isEqualTo(this.manufacturerIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getPrimaryAddress())
        .isEqualTo(this.primaryAddress);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getVersion())
        .isEqualTo(this.version);

    verify(this.deviceChannelsHelper, times(1))
        .findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class));
  }

  @Test
  public void testNoMatchAndNoEmptyChannel() throws ProtocolAdapterException {

    final MbusChannelElementsDto mbusChannelElementsDto =
        new MbusChannelElementsDto(
            (short) -1, "noMatch", "noMatch", "noMatch", (short) -1, (short) -1);

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(this.candidateChannelElementValues);

    final MbusChannelElementsResponseDto responseDto =
        this.commandExecutor.execute(
            this.conn, this.device, mbusChannelElementsDto, this.messageMetadata);

    assertThat(responseDto.getChannel()).isNull();
    assertThat(responseDto.getRetrievedChannelElements().get(0).getDeviceTypeIdentification())
        .isEqualTo(this.deviceTypeIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getIdentificationNumber())
        .isEqualTo(this.identificationNumber);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getManufacturerIdentification())
        .isEqualTo(this.manufacturerIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getPrimaryAddress())
        .isEqualTo(this.primaryAddress);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getVersion())
        .isEqualTo(this.version);

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

    when(this.deviceChannelsHelper.findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class)))
        .thenReturn(candidateChannelElementValuesWithEmptyChannel);
    when(this.deviceChannelsHelper.findEmptyChannel(candidateChannelElementValuesWithEmptyChannel))
        .thenReturn(emptyChannel);
    when(this.deviceChannelsHelper.writeUpdatedMbus(
            eq(this.conn),
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
    when(this.deviceChannelsHelper.correctFirstChannelOffset(any(ChannelElementValuesDto.class)))
        .thenReturn((short) (emptyChannel.getChannel() - 1));

    final MbusChannelElementsResponseDto responseDto =
        this.commandExecutor.execute(
            this.conn, this.device, mbusChannelElementsDto, this.messageMetadata);

    assertThat(responseDto.getChannel()).isEqualTo((short) 2);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getDeviceTypeIdentification())
        .isEqualTo(this.deviceTypeIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getIdentificationNumber())
        .isEqualTo(this.identificationNumber);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getManufacturerIdentification())
        .isEqualTo(this.manufacturerIdentification);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getPrimaryAddress())
        .isEqualTo(this.primaryAddress);
    assertThat(responseDto.getRetrievedChannelElements().get(0).getVersion())
        .isEqualTo(this.version);

    verify(this.deviceChannelsHelper, times(1))
        .findCandidateChannelsForDevice(
            eq(this.conn), eq(this.device), any(MbusChannelElementsDto.class));
  }
}
