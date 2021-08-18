/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.ObisCode;
import org.openmuc.jdlms.SetParameter;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.GetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.ProtocolServiceLookup;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration.service.SetConfigurationObjectService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class SetRandomisationSettingsCommandExecutorTest {

  @Mock private DlmsObjectConfigService dlmsObjectConfigService;

  @Mock private ProtocolServiceLookup protocolServiceLookup;

  @Mock private GetConfigurationObjectService getConfigurationObjectService;

  @Mock private SetConfigurationObjectService setConfigurationObjectService;

  @InjectMocks private SetRandomisationSettingsCommandExecutor executor;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private DlmsConnectionManager dlmsConnectionManager;

  private SetRandomisationSettingsRequestDataDto dataDto;
  private DlmsDevice device;
  private MessageMetadata messageMetadata;

  @BeforeEach
  public void init() throws ProtocolAdapterException, IOException {

    // SETUP
    final Protocol smr51 = Protocol.SMR_5_1;
    this.device = this.createDlmsDevice(smr51);

    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    final AttributeAddress address = new AttributeAddress(1, new ObisCode("0.1.94.31.12.255"), 1);

    this.dataDto = new SetRandomisationSettingsRequestDataDto(0, 1, 1, 1);

    final ConfigurationFlagsDto currentConfigurationFlagsDto =
        new ConfigurationFlagsDto(this.getFlags());
    final ConfigurationObjectDto currentConfigurationObjectDto =
        new ConfigurationObjectDto(currentConfigurationFlagsDto);

    when(this.protocolServiceLookup.lookupGetService(smr51))
        .thenReturn(this.getConfigurationObjectService);
    when(this.protocolServiceLookup.lookupSetService(smr51))
        .thenReturn(this.setConfigurationObjectService);
    when(this.getConfigurationObjectService.getConfigurationObject(this.dlmsConnectionManager))
        .thenReturn(currentConfigurationObjectDto);
    when(this.setConfigurationObjectService.setConfigurationObject(
            any(DlmsConnectionManager.class),
            any(ConfigurationObjectDto.class),
            any(ConfigurationObjectDto.class)))
        .thenReturn(AccessResultCode.SUCCESS);
    when(this.dlmsObjectConfigService.findAttributeAddress(
            this.device, DlmsObjectType.RANDOMISATION_SETTINGS, null))
        .thenReturn(Optional.of(address));

    when(this.dlmsConnectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
  }

  @Test
  public void testExecuteSuccess() throws ProtocolAdapterException {

    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(
            this.dlmsConnectionManager, this.device, this.dataDto, this.messageMetadata);

    // ASSERT
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  @Test
  public void testExecuteFailConfiguration() throws ProtocolAdapterException {
    // SETUP
    when(this.setConfigurationObjectService.setConfigurationObject(
            any(DlmsConnectionManager.class),
            any(ConfigurationObjectDto.class),
            any(ConfigurationObjectDto.class)))
        .thenReturn(AccessResultCode.OTHER_REASON);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.executor.execute(
                  this.dlmsConnectionManager, this.device, this.dataDto, this.messageMetadata);
            });
  }

  @Test
  public void testExecuteFailSetRandomisationSettings()
      throws ProtocolAdapterException, IOException {

    // SETUP
    when(this.dlmsConnection.set(any(SetParameter.class)))
        .thenReturn(AccessResultCode.OTHER_REASON);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.executor.execute(
                  this.dlmsConnectionManager, this.device, this.dataDto, this.messageMetadata);
            });
  }

  @Test
  public void testUnknownAttribute() throws ProtocolAdapterException {

    // SETUP
    when(this.dlmsObjectConfigService.findAttributeAddress(
            this.device, DlmsObjectType.RANDOMISATION_SETTINGS, null))
        .thenReturn(Optional.empty());

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.executor.execute(
                  this.dlmsConnectionManager, this.device, this.dataDto, this.messageMetadata);
            });
  }

  private DlmsDevice createDlmsDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    device.setSelectiveAccessSupported(true);
    return device;
  }

  private List<ConfigurationFlagDto> getFlags() {

    return Arrays.stream(ConfigurationFlagTypeDto.values())
        .map(flagType -> new ConfigurationFlagDto(flagType, true))
        .collect(Collectors.toList());
  }
}
