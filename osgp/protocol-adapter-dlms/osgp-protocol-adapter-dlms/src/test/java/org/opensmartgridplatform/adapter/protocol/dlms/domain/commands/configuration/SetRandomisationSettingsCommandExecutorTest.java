// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
import org.opensmartgridplatform.dlms.objectconfig.DlmsObjectType;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagTypeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationFlagsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ConfigurationObjectDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SetRandomisationSettingsRequestDataDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class SetRandomisationSettingsCommandExecutorTest {

  @Mock private ProtocolServiceLookup protocolServiceLookup;

  @Mock private GetConfigurationObjectService getConfigurationObjectService;

  @Mock private SetConfigurationObjectService setConfigurationObjectService;

  @Mock private ObjectConfigServiceHelper objectConfigServiceHelper;

  @InjectMocks private SetRandomisationSettingsCommandExecutor executor;

  @Mock private DlmsConnection dlmsConnection;

  @Mock private DlmsConnectionManager dlmsConnectionManager;

  private SetRandomisationSettingsRequestDataDto dataDto;
  private DlmsDevice device;
  private MessageMetadata messageMetadata;

  public void init(final Protocol protocol) throws ProtocolAdapterException, IOException {

    this.device = this.createDlmsDevice(protocol);

    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    this.dataDto = new SetRandomisationSettingsRequestDataDto(0, 1, 1, 1);

    final ConfigurationFlagsDto currentConfigurationFlagsDto =
        new ConfigurationFlagsDto(this.getFlags());
    final ConfigurationObjectDto currentConfigurationObjectDto =
        new ConfigurationObjectDto(currentConfigurationFlagsDto);

    when(this.protocolServiceLookup.lookupGetService(protocol))
        .thenReturn(this.getConfigurationObjectService);
    when(this.protocolServiceLookup.lookupSetService(protocol))
        .thenReturn(this.setConfigurationObjectService);
    when(this.getConfigurationObjectService.getConfigurationObject(
            this.dlmsConnectionManager, protocol, this.device))
        .thenReturn(currentConfigurationObjectDto);
    when(this.setConfigurationObjectService.setConfigurationObject(
            any(DlmsConnectionManager.class),
            any(ConfigurationObjectDto.class),
            any(ConfigurationObjectDto.class),
            eq(protocol),
            eq(this.device)))
        .thenReturn(AccessResultCode.SUCCESS);

    when(this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            protocol, DlmsObjectType.RANDOMISATION_SETTINGS))
        .thenReturn(Optional.of(new AttributeAddress(-1, (ObisCode) null, -1)));

    when(this.dlmsConnectionManager.getConnection()).thenReturn(this.dlmsConnection);
    when(this.dlmsConnection.set(any(SetParameter.class))).thenReturn(AccessResultCode.SUCCESS);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_4_2_2", "SMR_5_0_0"})
  void testExecuteSuccess(final Protocol protocol) throws ProtocolAdapterException, IOException {

    this.init(protocol);
    // CALL
    final AccessResultCode resultCode =
        this.executor.execute(
            this.dlmsConnectionManager, this.device, this.dataDto, this.messageMetadata);

    // ASSERT
    assertThat(resultCode).isEqualTo(AccessResultCode.SUCCESS);
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_4_2_2", "SMR_5_0_0"})
  void testExecuteFailConfiguration(final Protocol protocol)
      throws ProtocolAdapterException, IOException {
    // SETUP
    this.init(protocol);

    when(this.setConfigurationObjectService.setConfigurationObject(
            any(DlmsConnectionManager.class),
            any(ConfigurationObjectDto.class),
            any(ConfigurationObjectDto.class),
            eq(protocol),
            eq(this.device)))
        .thenReturn(AccessResultCode.OTHER_REASON);

    assertThatExceptionOfType(ProtocolAdapterException.class)
        .isThrownBy(
            () -> {
              // CALL
              this.executor.execute(
                  this.dlmsConnectionManager, this.device, this.dataDto, this.messageMetadata);
            });
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_4_2_2", "SMR_5_0_0"})
  void testExecuteFailSetRandomisationSettings(final Protocol protocol)
      throws ProtocolAdapterException, IOException {

    // SETUP
    this.init(protocol);

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

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_4_2_2", "SMR_5_0_0"})
  void testUnknownAttribute(final Protocol protocol) throws ProtocolAdapterException, IOException {

    // SETUP
    this.init(protocol);

    when(this.objectConfigServiceHelper.findOptionalDefaultAttributeAddress(
            protocol, DlmsObjectType.RANDOMISATION_SETTINGS))
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
        .toList();
  }
}
