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

class SetAlarmNotificationsCommandExecutorRegister3Test {
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
            SetAlarmNotificationsCommandExecutorRegister3Test.this.setParametersReceived.add(
                setParameter);
            return AccessResultCode.SUCCESS;
          }
        };

    // Set the return value for alarm register 1 to 10 (0b1010):
    // Enabled events: REPLACE_BATTERY and AUXILIARY_EVENT
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.10.255", 2), DataObject.newInteger32Data(10));
    // Set the return value for alarm register 2 to 0 (no alarms set):
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.11.255", 2), DataObject.newInteger32Data(0));

    this.connMgr = new DlmsConnectionManagerStub(this.conn);
  }

  @ParameterizedTest
  @CsvSource({
    "1,LAST_GASP", // 0b00000001
    "2,LAST_GASP_TEST", // 0b00000010
    "3,LAST_GASP;LAST_GASP_TEST", // 0b00000011
  })
  void testSetSettingEnabledRegisterAlarmRegister3(
      final long expectedValue, final String alarmTypesInput) throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_5);

    // Set the return value for alarm register 3 to 0 (no alarms set):
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.12.255", 2), DataObject.newInteger32Data(0));

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
    "2,LAST_GASP", // 0b00000010
    "1,LAST_GASP_TEST", // 0b00000001
    "0,LAST_GASP;LAST_GASP_TEST", // 0b00000000
  })
  void testSetSettingDisabledRegisterAlarmRegister3(
      final long expectedValue, final String alarmTypesInput) throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_5);

    // Set the return value for alarm register 3 to 3 (all alarms (LAST_GASP & LAST_GASP_TEST) set)
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.12.255", 2), DataObject.newInteger32Data(3));

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
  void testSetSettingThatIsAlreadySetInAlarmRegister3() throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_5);

    // Set the return value for alarm register 3 to 3 (all alarms (LAST_GASP & LAST_GASP_TEST) set)
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.12.255", 2), DataObject.newInteger32Data(3));

    // Setting notifications that are not different from what is on the meter already,
    // should always be successful.
    final AlarmNotificationDto[] alarmNotificationDtos =
        new AlarmNotificationDto[] {
          new AlarmNotificationDto(AlarmTypeDto.LAST_GASP, true),
          new AlarmNotificationDto(AlarmTypeDto.LAST_GASP_TEST, true)
        };
    final AccessResultCode res = this.execute(device, alarmNotificationDtos);
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    // Since nothing changed, not a single message should have been sent to the meter.
    assertThat(this.setParametersReceived.size()).isZero();
  }

  @Test
  void testSetDisabledThatIsAlreadySetDisabledInAlarmRegister3() throws OsgpException {
    final DlmsDevice device = this.createDevice(Protocol.SMR_5_5);

    // Set the return value for alarm register 3 to 0 (no alarms set):
    this.conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.12.255", 2), DataObject.newInteger32Data(0));

    // Setting notifications that are not different from what is on the meter already,
    // should always be successful.
    final AlarmNotificationDto[] alarmNotificationDtos =
        new AlarmNotificationDto[] {
          new AlarmNotificationDto(AlarmTypeDto.LAST_GASP, false),
          new AlarmNotificationDto(AlarmTypeDto.LAST_GASP_TEST, false)
        };
    final AccessResultCode res = this.execute(device, alarmNotificationDtos);
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    // Since nothing changed, not a single message should have been sent to the meter.
    assertThat(this.setParametersReceived.size()).isZero();
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
