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

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.GetResult;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelElementValuesDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponseDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
public class CoupleMbusDeviceByChannelCommandExecutorTest {

  @Mock private DeviceChannelsHelper deviceChannelsHelper;

  @Mock private DlmsConnectionManager conn;

  @Mock private DlmsDevice device;

  @Mock private CoupleMbusDeviceByChannelRequestDataDto coupleMbusDeviceByChannelRequestDataDto;

  @InjectMocks
  private CoupleMbusDeviceByChannelCommandExecutor commandExecutor =
      new CoupleMbusDeviceByChannelCommandExecutor();

  @Test
  public void testHappyFlow() throws ProtocolAdapterException {

    final short channel = (short) 1;
    final Short primaryAddress = 9;
    final String manufacturerIdentification = "manufacturerIdentification";
    final short version = 123;
    final short deviceTypeIdentification = 456;
    final String identificationNumber = "identificationNumber";
    final List<GetResult> resultList = new ArrayList<>();

    final ChannelElementValuesDto dto =
        new ChannelElementValuesDto(
            channel,
            primaryAddress,
            identificationNumber,
            manufacturerIdentification,
            version,
            deviceTypeIdentification);

    final MessageMetadata messageMetadata =
        MessageMetadata.newMessageMetadataBuilder().withCorrelationUid("123456").build();

    when(this.coupleMbusDeviceByChannelRequestDataDto.getChannel()).thenReturn(channel);
    when(this.deviceChannelsHelper.getMBusClientAttributeValues(this.conn, this.device, channel))
        .thenReturn(resultList);
    when(this.deviceChannelsHelper.makeChannelElementValues(channel, resultList)).thenReturn(dto);

    final CoupleMbusDeviceByChannelResponseDto responseDto =
        this.commandExecutor.execute(
            this.conn, this.device, this.coupleMbusDeviceByChannelRequestDataDto, messageMetadata);

    assertThat(responseDto).isNotNull();
    assertThat(responseDto.getChannelElementValues()).isNotNull();
    assertThat(responseDto.getChannelElementValues().getChannel()).isEqualTo(channel);
    assertThat(responseDto.getChannelElementValues().getDeviceTypeIdentification())
        .isEqualTo(deviceTypeIdentification);
    assertThat(responseDto.getChannelElementValues().getIdentificationNumber())
        .isEqualTo(identificationNumber);
    assertThat(responseDto.getChannelElementValues().getManufacturerIdentification())
        .isEqualTo(manufacturerIdentification);
    assertThat(responseDto.getChannelElementValues().getPrimaryAddress()).isEqualTo(primaryAddress);
    assertThat(responseDto.getChannelElementValues().getVersion()).isEqualTo(version);

    verify(this.deviceChannelsHelper, times(1))
        .getMBusClientAttributeValues(eq(this.conn), eq(this.device), any(Short.class));
    verify(this.deviceChannelsHelper, times(1)).makeChannelElementValues(channel, resultList);
  }
}
