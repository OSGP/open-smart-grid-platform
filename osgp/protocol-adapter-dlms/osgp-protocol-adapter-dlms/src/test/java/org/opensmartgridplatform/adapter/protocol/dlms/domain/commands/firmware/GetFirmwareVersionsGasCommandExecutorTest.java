// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.firmware;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.dto.valueobjects.FirmwareModuleType.SIMPLE_VERSION_INFO;

import java.io.IOException;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.GetResult;
import org.openmuc.jdlms.ObisCode;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.testutil.GetResultBuilder;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.ObjectConfigServiceHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.NotSupportedByProtocolException;
import org.opensmartgridplatform.adapter.protocol.dlms.infra.messaging.DlmsMessageListener;
import org.opensmartgridplatform.dlms.exceptions.ObjectConfigException;
import org.opensmartgridplatform.dlms.services.ObjectConfigService;
import org.opensmartgridplatform.dto.valueobjects.FirmwareVersionGasDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ChannelDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.GetFirmwareVersionQueryDto;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

@ExtendWith(MockitoExtension.class)
class GetFirmwareVersionsGasCommandExecutorTest {
  private static final int CLASS_ID = 4;
  private static final int ATTRIBUTE_ID = 2;

  private static final String OBIS_CODE = "0.x.24.2.11.255";

  private static final String VERSION = "A1B2C3";
  private static final String DEVICE_IDENTIFICATION = "12345";

  private GetFirmwareVersionsGasCommandExecutor executor;

  @Mock private DlmsMessageListener listener;

  @Mock private DlmsHelper dlmsHelper;

  private DlmsConnectionManager connectionHolder;

  private MessageMetadata messageMetadata;

  @BeforeEach
  public void setUp() throws IOException, ObjectConfigException {
    final ObjectConfigService objectConfigService = new ObjectConfigService();
    final ObjectConfigServiceHelper objectConfigServiceHelper =
        new ObjectConfigServiceHelper(objectConfigService);

    this.executor =
        new GetFirmwareVersionsGasCommandExecutor(this.dlmsHelper, objectConfigServiceHelper);
    this.connectionHolder = new DlmsConnectionManager(null, null, null, this.listener, null);
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "DSMR_4_2_2", "SMR_4_3", "OTHER_PROTOCOL"},
      mode = Mode.EXCLUDE)
  void returnsFirmwareVersionForSupportedProtocols(final Protocol protocol) throws Exception {
    this.returnsFirmwareVersion(protocol, ChannelDto.ONE);
  }

  @ParameterizedTest
  @EnumSource(ChannelDto.class)
  void returnsFirmwareVersionForAllChannels(final ChannelDto channel) throws Exception {
    this.returnsFirmwareVersion(Protocol.SMR_5_0_0, channel);
  }

  void returnsFirmwareVersion(final Protocol protocol, final ChannelDto channel) throws Exception {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    final GetFirmwareVersionQueryDto queryDto =
        new GetFirmwareVersionQueryDto(channel, DEVICE_IDENTIFICATION);
    final ObisCode obis =
        new ObisCode(OBIS_CODE.replace("x", String.valueOf(channel.getChannelNumber())));

    final GetResult getResult = new GetResultBuilder().build();

    when(this.dlmsHelper.getAndCheck(
            same(this.connectionHolder),
            same(device),
            eq("retrieve firmware versions"),
            refEq(new AttributeAddress(CLASS_ID, obis, ATTRIBUTE_ID))))
        .thenReturn(List.of(getResult));
    when(this.dlmsHelper.readHexString(
            getResult.getResultData(), SIMPLE_VERSION_INFO.getDescription()))
        .thenReturn(VERSION);

    final FirmwareVersionGasDto result =
        this.executor.execute(this.connectionHolder, device, queryDto, this.messageMetadata);

    Assertions.assertThat(result)
        .usingRecursiveComparison()
        .isEqualTo(
            new FirmwareVersionGasDto(
                SIMPLE_VERSION_INFO, VERSION, queryDto.getMbusDeviceIdentification()));
  }

  @ParameterizedTest
  @EnumSource(
      value = Protocol.class,
      names = {"DSMR_2_2", "DSMR_4_2_2", "SMR_4_3"},
      mode = Mode.INCLUDE)
  void returnsErrorForNotSupportedProtocols(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);
    final GetFirmwareVersionQueryDto queryDto =
        new GetFirmwareVersionQueryDto(ChannelDto.ONE, DEVICE_IDENTIFICATION);

    assertThrows(
        NotSupportedByProtocolException.class,
        () -> this.executor.execute(this.connectionHolder, device, queryDto, this.messageMetadata));
  }
}
