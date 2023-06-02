//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openmuc.jdlms.AccessResultCode;
import org.openmuc.jdlms.AttributeAddress;
import org.openmuc.jdlms.SetParameter;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.CommandExecutor;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigConfiguration;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectConfigService;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.Protocol;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

class SetAlarmNotificationsCommandExecutorTest {
  private CommandExecutor<AlarmNotificationsDto, AccessResultCode> executor;
  private List<SetParameter> setParametersReceived;
  private DlmsConnectionManager connMgr;
  private MessageMetadata messageMetadata;
  private DlmsConnectionStub conn;

  @BeforeEach
  public void setUp() {
    this.setParametersReceived = new ArrayList<>();
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    final DlmsObjectConfigConfiguration dlmsObjectConfigConfiguration =
        new DlmsObjectConfigConfiguration();
    final DlmsObjectConfigService dlmsObjectConfigService =
        new DlmsObjectConfigService(
            new DlmsHelper(), dlmsObjectConfigConfiguration.getDlmsObjectConfigs());
    this.executor = new SetAlarmNotificationsCommandExecutor(dlmsObjectConfigService);

    this.conn =
        new DlmsConnectionStub() {
          @Override
          public AccessResultCode set(final SetParameter setParameter) {
            SetAlarmNotificationsCommandExecutorTest.this.setParametersReceived.add(setParameter);
            return AccessResultCode.SUCCESS;
          }
        };

    // Set the return value for alarm register 1 to 10 (0b1010):
    // Enabled events: REPLACE_BATTERY and AUXILIARY_EVENT
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.10.255", 2), DataObject.newInteger32Data(10));

    this.connMgr = new DlmsConnectionManagerStub(this.conn);
  }

  @Test
  void testSetSettingThatIsAlreadySet() throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_0_0);

    // Setting notifications that are not different from what is on the meter already,
    // should always be successful.
    final AccessResultCode res =
        this.execute(device, new AlarmNotificationDto(AlarmTypeDto.REPLACE_BATTERY, true));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    // Since nothing changed, not a single message should have been sent to the meter.
    assertThat(this.setParametersReceived.size()).isZero();
  }

  @Test
  void testSetSettingEnabled() throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_0_0);

    // Now we enable something: CLOCK_INVALID to enabled.
    final AccessResultCode res =
        this.execute(device, new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    // Expecting 11 (0b1011).
    assertThat((long) this.setParametersReceived.get(0).getData().getValue()).isEqualTo(11);
  }

  @ParameterizedTest
  @CsvSource({
    "1,VOLTAGE_SAG_IN_PHASE_DETECTED_L1", // 0b00000001
    "2,VOLTAGE_SAG_IN_PHASE_DETECTED_L2", // 0b00000010
    "4,VOLTAGE_SAG_IN_PHASE_DETECTED_L3", // 0b00000100
    "8,VOLTAGE_SWELL_IN_PHASE_DETECTED_L1", // 0b00001000
    "16,VOLTAGE_SWELL_IN_PHASE_DETECTED_L2", // 0b00010000
    "32,VOLTAGE_SWELL_IN_PHASE_DETECTED_L3", // 0b00100000
    "3,VOLTAGE_SAG_IN_PHASE_DETECTED_L1;VOLTAGE_SAG_IN_PHASE_DETECTED_L2", // 0b00000011
    "7,VOLTAGE_SAG_IN_PHASE_DETECTED_L1;VOLTAGE_SAG_IN_PHASE_DETECTED_L2;VOLTAGE_SAG_IN_PHASE_DETECTED_L3" // 0b00000111
  })
  void testSetSettingEnabledRegisterAlarmRegister2(
      final long expectedValue, final String alarmTypesInput) throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_2);

    // Set the return value for alarm register 2 to 0 (no alarms set):
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.11.255", 2), DataObject.newInteger32Data(0));

    final List<AlarmTypeDto> alarmTypes =
        Arrays.stream(alarmTypesInput.split(";")).map(AlarmTypeDto::valueOf).collect(toList());
    final AccessResultCode res =
        this.execute(
            device,
            alarmTypes.stream()
                .map(alarmType -> new AlarmNotificationDto(alarmType, true))
                .toArray(AlarmNotificationDto[]::new));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    assertThat((long) this.setParametersReceived.get(0).getData().getValue())
        .isEqualTo(expectedValue);
  }

  @ParameterizedTest
  @CsvSource({
    "62,VOLTAGE_SAG_IN_PHASE_DETECTED_L1", // 0b00111110
    "61,VOLTAGE_SAG_IN_PHASE_DETECTED_L2", // 0b00111101
    "59,VOLTAGE_SAG_IN_PHASE_DETECTED_L3", // 0b00111011
    "55,VOLTAGE_SWELL_IN_PHASE_DETECTED_L1", // 0b00110111
    "47,VOLTAGE_SWELL_IN_PHASE_DETECTED_L2", // 0b00101111
    "31,VOLTAGE_SWELL_IN_PHASE_DETECTED_L3", // 0b00011111
    "60,VOLTAGE_SAG_IN_PHASE_DETECTED_L1;VOLTAGE_SAG_IN_PHASE_DETECTED_L2", // 0b00111100
    "56,VOLTAGE_SAG_IN_PHASE_DETECTED_L1;VOLTAGE_SAG_IN_PHASE_DETECTED_L2;VOLTAGE_SAG_IN_PHASE_DETECTED_L3" // 0b00111000
  })
  void testSetSettingDisabledRegisterAlarmRegister2(
      final long expectedValue, final String alarmTypesInput) throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_2);

    // Set the return value for alarm register 2 to 3F (all alarms set):
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.11.255", 2), DataObject.newInteger32Data(0x3F));

    final List<AlarmTypeDto> alarmTypes =
        Arrays.stream(alarmTypesInput.split(";")).map(AlarmTypeDto::valueOf).collect(toList());
    final AccessResultCode res =
        this.execute(
            device,
            alarmTypes.stream()
                .map(alarmType -> new AlarmNotificationDto(alarmType, false))
                .toArray(AlarmNotificationDto[]::new));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    assertThat((long) this.setParametersReceived.get(0).getData().getValue())
        .isEqualTo(expectedValue);
  }

  @Test
  void testSetSettingThatIsAlreadySetInAlarmRegister2() throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_2);

    // Set the return value for alarm register 2 to 3F (all alarms set):
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.11.255", 2), DataObject.newInteger32Data(0x3F));

    // Setting notifications that are not different from what is on the meter already,
    // should always be successful.
    final AccessResultCode res =
        this.execute(
            device,
            new AlarmNotificationDto(AlarmTypeDto.VOLTAGE_SWELL_IN_PHASE_DETECTED_L3, true));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    // Since nothing changed, not a single message should have been sent to the meter.
    assertThat(this.setParametersReceived.size()).isZero();
  }

  @Test
  void testSetSettingEnabledAndDisabled() throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_0_0);

    // Both enable and disable in one call:
    // CLOCK_INVALID to enabled and REPLACE_BATTERY to disabled.
    final AccessResultCode res =
        this.execute(
            device,
            new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true),
            new AlarmNotificationDto(AlarmTypeDto.REPLACE_BATTERY, false));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    // Expecting 9 (0b1001).
    assertThat((long) this.setParametersReceived.get(0).getData().getValue()).isEqualTo(9);
  }

  private AccessResultCode execute(
      final DlmsDevice device, final AlarmNotificationDto... alarmNotificationDtos)
      throws OsgpException {
    final Set<AlarmNotificationDto> alarmNotificationDtoSet =
        new HashSet<>(Arrays.asList(alarmNotificationDtos));
    final AlarmNotificationsDto alarmNotificationsDto =
        new AlarmNotificationsDto(alarmNotificationDtoSet);
    return this.executor.execute(this.connMgr, device, alarmNotificationsDto, this.messageMetadata);
  }

  private DlmsDevice createDevice(final Protocol protocol) {
    final DlmsDevice device = new DlmsDevice();
    device.setProtocol(protocol);

    return device;
  }
}
