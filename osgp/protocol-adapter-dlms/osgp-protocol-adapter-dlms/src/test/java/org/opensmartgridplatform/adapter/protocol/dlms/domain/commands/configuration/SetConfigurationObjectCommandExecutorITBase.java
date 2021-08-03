/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GprsOperationModeTypeDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

public abstract class SetConfigurationObjectCommandExecutorITBase {

  SetConfigurationObjectCommandExecutor instance;
  @Mock DlmsConnectionManager conn;
  @Mock DlmsConnection dlmsConnection;
  @Mock GetResult getResult;
  @Captor ArgumentCaptor<SetParameter> setParameterArgumentCaptor;

  @Mock private DlmsMessageListener dlmsMessageListener;
  MessageMetadata messageMetadata;

  public void setUp(
      final GetConfigurationObjectService getService,
      final SetConfigurationObjectService setService)
      throws IOException {
    final List<ProtocolService> protocolServices = new ArrayList<>();
    protocolServices.add(getService);
    protocolServices.add(setService);
    final ProtocolServiceLookup protocolServiceLookup = new ProtocolServiceLookup(protocolServices);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    this.instance = new SetConfigurationObjectCommandExecutor(protocolServiceLookup);

    when(this.conn.getConnection()).thenReturn(this.dlmsConnection);
    when(this.conn.getDlmsMessageListener()).thenReturn(this.dlmsMessageListener);
    when(this.dlmsConnection.get(any(AttributeAddress.class))).thenReturn(this.getResult);
    when(this.getResult.getResultCode()).thenReturn(AccessResultCode.SUCCESS);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
  }

  abstract Integer getBitPosition(final ConfigurationFlagTypeDto flag);

  byte asByte(final String bits) {
    return (byte) Integer.parseInt(bits, 2);
  }

  ConfigurationFlagDto createFlagDto(
      final ConfigurationFlagTypeDto flagType, final boolean enabled) {
    return new ConfigurationFlagDto(flagType, enabled);
  }

  ConfigurationObjectDto createConfigurationObjectDto(
      final GprsOperationModeTypeDto gprsMode,
      final ConfigurationFlagDto... configurationFlagDtos) {
    final List<ConfigurationFlagDto> flags = new ArrayList<>(Arrays.asList(configurationFlagDtos));
    final ConfigurationFlagsDto configurationFlags = new ConfigurationFlagsDto(flags);
    return new ConfigurationObjectDto(gprsMode, configurationFlags);
  }

  byte[] createFlagBytes(final ConfigurationFlagTypeDto... flags) {
    return this.toBytes(this.createWord(flags));
  }

  private String createWord(final ConfigurationFlagTypeDto[] flags) {
    final StringBuilder sb = new StringBuilder("0000000000000000");
    for (final ConfigurationFlagTypeDto flag : flags) {
      sb.setCharAt(this.getBitPosition(flag), '1');
    }
    return sb.toString();
  }

  private byte[] toBytes(final String word) {
    final byte[] byteArray = new byte[2];
    for (int index = 0; index < word.length(); index += 8) {
      byteArray[index / 8] = (byte) Integer.parseInt(word.substring(index, index + 8), 2);
    }
    return byteArray;
  }
}
