/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.alarm;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.DlmsObjectType;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionManagerStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.stub.DlmsConnectionStub;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.utils.DlmsHelper;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.factories.DlmsConnectionManager;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.AlarmTypeDto;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.MessageMetadata;

class SetAlarmNotificationsCommandExecutorTest {
  private DlmsDevice device;
  private CommandExecutor<AlarmNotificationsDto, AccessResultCode> executor;
  private List<SetParameter> setParametersReceived;
  private DlmsConnectionManager connMgr;
  private MessageMetadata messageMetadata;

  @BeforeEach
  public void setUp() {
    this.setParametersReceived = new ArrayList<>();
    this.device = new DlmsDevice("SuperAwesomeHeroicRockstarDevice");
    this.messageMetadata = MessageMetadata.newBuilder().withCorrelationUid("123456").build();

    final DlmsObjectConfigConfiguration dlmsObjectConfigConfiguration =
        new DlmsObjectConfigConfiguration();
    final DlmsObjectConfigService dlmsObjectConfigService =
        new DlmsObjectConfigService(
            new DlmsHelper(), dlmsObjectConfigConfiguration.getDlmsObjectConfigs()) {
          @Override
          public Optional<AttributeAddress> findAttributeAddress(
              final DlmsDevice device, final DlmsObjectType type, final Integer channel) {
            switch (type) {
              case ALARM_FILTER_1:
                return Optional.of(new AttributeAddress(40, "0.0.97.98.10.255", 2));
              case ALARM_FILTER_2:
                return Optional.of(new AttributeAddress(40, "0.0.97.98.11.255", 2));
            }
            return Optional.empty();
          }
        };
    this.executor = new SetAlarmNotificationsCommandExecutor(dlmsObjectConfigService);

    final DlmsConnectionStub conn =
        new DlmsConnectionStub() {
          @Override
          public AccessResultCode set(final SetParameter setParameter) {
            SetAlarmNotificationsCommandExecutorTest.this.setParametersReceived.add(setParameter);
            return AccessResultCode.SUCCESS;
          }
        };

    // Set the return value to 10 (0b1010):
    // REPLACE_BATTERY enabled, AUXILIARY_EVENT enabled.
    conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.10.255", 2), DataObject.newInteger32Data(10));
    conn.addReturnValue(
        new AttributeAddress(1, "0.0.97.98.11.255", 2), DataObject.newInteger32Data(0));

    this.connMgr = new DlmsConnectionManagerStub(conn);
  }

  @Test
  void testSetSettingThatIsAlreadySet() throws OsgpException {
    // Setting notifications that are not different from what is on the
    // meter already,
    // should always be successful.
    final AccessResultCode res =
        this.execute(new AlarmNotificationDto(AlarmTypeDto.REPLACE_BATTERY, true));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    // Since nothing changed, not a single message should have been sent to
    // the meter.
    assertThat(this.setParametersReceived.size()).isZero();
  }

  @Test
  void testSetSettingEnabled() throws OsgpException {
    // Now we enable something: CLOCK_INVALID to enabled.
    final AccessResultCode res =
        this.execute(new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    // Expecting 11 (0b1011).
    assertThat((long) this.setParametersReceived.get(0).getData().getValue()).isEqualTo(11);
  }

  @ParameterizedTest
  @CsvSource({
    "1,VOLTAGE_SAG_IN_PHASE_DETECTED_L1",
    "2,VOLTAGE_SAG_IN_PHASE_DETECTED_L2",
    "4,VOLTAGE_SAG_IN_PHASE_DETECTED_L3",
    "8,VOLTAGE_SWELL_IN_PHASE_DETECTED_L1",
    "16,VOLTAGE_SWELL_IN_PHASE_DETECTED_L2",
    "32,VOLTAGE_SWELL_IN_PHASE_DETECTED_L3",
    "3,VOLTAGE_SAG_IN_PHASE_DETECTED_L1;VOLTAGE_SAG_IN_PHASE_DETECTED_L2",
    "7,VOLTAGE_SAG_IN_PHASE_DETECTED_L1;VOLTAGE_SAG_IN_PHASE_DETECTED_L2;VOLTAGE_SAG_IN_PHASE_DETECTED_L3"
  })
  void testSetSettingEnabledRegister(final long expectedValue, final String alarmTypesInput)
      throws OsgpException {
    final List<AlarmTypeDto> alarmTypes =
        Arrays.stream(alarmTypesInput.split(";")).map(AlarmTypeDto::valueOf).collect(toList());
    final AccessResultCode res =
        this.execute(
            alarmTypes.stream()
                .map(alarmType -> new AlarmNotificationDto(alarmType, true))
                .toArray(AlarmNotificationDto[]::new));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    assertThat((long) this.setParametersReceived.get(0).getData().getValue())
        .isEqualTo(expectedValue);
  }

  @Test
  void testSetSettingEnabledAndDisabled() throws OsgpException {
    // Now we enable and disable something: CLOCK_INVALID to enabled and
    // REPLACE_BATTERY to disabled.
    final AccessResultCode res =
        this.execute(
            new AlarmNotificationDto(AlarmTypeDto.CLOCK_INVALID, true),
            new AlarmNotificationDto(AlarmTypeDto.REPLACE_BATTERY, false));
    assertThat(res).isEqualTo(AccessResultCode.SUCCESS);
    assertThat(this.setParametersReceived.size()).isEqualTo(1);
    // Expecting 9 (0b1001).
    assertThat((long) this.setParametersReceived.get(0).getData().getValue()).isEqualTo(9);
  }

  private AccessResultCode execute(final AlarmNotificationDto... alarmNotificationDtos)
      throws OsgpException {
    final Set<AlarmNotificationDto> alarmNotificationDtoSet =
        new HashSet<>(Arrays.asList(alarmNotificationDtos));
    final AlarmNotificationsDto alarmNotificationsDto =
        new AlarmNotificationsDto(alarmNotificationDtoSet);
    return this.executor.execute(
        this.connMgr, this.device, alarmNotificationsDto, this.messageMetadata);
  }
}
