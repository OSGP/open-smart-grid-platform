//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.ClockStatus;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDate;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemDateTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObisCode;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemObjectDefinition;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.CosemTime;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WindowElement;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WindowElementDto;

public class PushSetupAlarmMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  // To test if a PushSetupAlarm can be mapped if instance variables are null.
  @Test
  public void testPushSetupAlarmMappingNull() {

    // build test data
    final PushSetupAlarm pushSetupAlarm = new PushSetupAlarmBuilder().withNullValues().build();

    // actual mapping
    final PushSetupAlarmDto pushSetupAlarmDto =
        this.configurationMapper.map(pushSetupAlarm, PushSetupAlarmDto.class);

    // check values
    assertThat(pushSetupAlarmDto).isNotNull();
    assertThat(pushSetupAlarmDto.getLogicalName()).isNull();
    assertThat(pushSetupAlarmDto.getPushObjectList()).isNull();
    assertThat(pushSetupAlarmDto.getSendDestinationAndMethod()).isNull();
    assertThat(pushSetupAlarmDto.getCommunicationWindow()).isNull();
    assertThat(pushSetupAlarmDto.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupAlarmDto.getNumberOfRetries()).isNull();
    assertThat(pushSetupAlarmDto.getRepetitionDelay()).isNull();
  }

  // To test if a PushSetupAlarm can be mapped if instance variables are
  // initialized and lists are empty.
  @Test
  public void testPushSetupAlarmMappingWithEmptyLists() {

    // build test data
    final ArrayList<CosemObjectDefinition> pushObjectList = new ArrayList<>();
    final ArrayList<WindowElement> communicationWindow = new ArrayList<>();

    final PushSetupAlarm pushSetupAlarm =
        new PushSetupAlarmBuilder().withEmptyLists(pushObjectList, communicationWindow).build();

    // actual mapping
    final PushSetupAlarmDto pushSetupAlarmDto =
        this.configurationMapper.map(pushSetupAlarm, PushSetupAlarmDto.class);

    // check values
    this.checkCosemObisCodeMapping(
        pushSetupAlarm.getLogicalName(), pushSetupAlarmDto.getLogicalName());
    this.checkSendDestinationAndMethodMapping(pushSetupAlarm, pushSetupAlarmDto);
    this.checkIntegerMapping(pushSetupAlarm, pushSetupAlarmDto);

    assertThat(pushSetupAlarmDto.getPushObjectList()).isNotNull();
    assertThat(pushSetupAlarmDto.getCommunicationWindow()).isNotNull();
  }

  // To test Mapping if lists contain values
  @Test
  public void testPushSetupAlarmMappingWithLists() {

    // build test data
    final CosemObisCode logicalName = new CosemObisCode(new int[] {1, 2, 3, 4, 5, 6});
    final CosemObjectDefinition cosemObjectDefinition =
        new CosemObjectDefinition(1, logicalName, 2);
    final CosemDateTime startTime =
        new CosemDateTime(
            new CosemDate(2016, 3, 17),
            new CosemTime(11, 52, 45),
            0,
            new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
    final CosemDateTime endTime =
        new CosemDateTime(
            new CosemDate(2016, 3, 17),
            new CosemTime(11, 52, 45),
            0,
            new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
    final WindowElement windowElement = new WindowElement(startTime, endTime);

    final PushSetupAlarm pushSetupAlarm =
        new PushSetupAlarmBuilder().withFilledLists(cosemObjectDefinition, windowElement).build();

    // actual mapping
    final PushSetupAlarmDto pushSetupAlarmDto =
        this.configurationMapper.map(pushSetupAlarm, PushSetupAlarmDto.class);

    // check values
    this.checkCosemObisCodeMapping(
        pushSetupAlarm.getLogicalName(), pushSetupAlarmDto.getLogicalName());
    this.checkSendDestinationAndMethodMapping(pushSetupAlarm, pushSetupAlarmDto);
    this.checkIntegerMapping(pushSetupAlarm, pushSetupAlarmDto);
    this.checkNonEmptyListMapping(pushSetupAlarm, pushSetupAlarmDto);
  }

  // method to test Integer object mapping
  private void checkIntegerMapping(
      final PushSetupAlarm pushSetupAlarm, final PushSetupAlarmDto pushSetupAlarmDto) {

    // make sure none is null
    assertThat(pushSetupAlarmDto.getRandomisationStartInterval()).isNotNull();
    assertThat(pushSetupAlarmDto.getNumberOfRetries()).isNotNull();
    assertThat(pushSetupAlarmDto.getRepetitionDelay()).isNotNull();

    // make sure all values are equal
    assertThat(pushSetupAlarmDto.getRandomisationStartInterval())
        .isEqualTo(pushSetupAlarm.getRandomisationStartInterval());
    assertThat(pushSetupAlarmDto.getNumberOfRetries())
        .isEqualTo(pushSetupAlarm.getNumberOfRetries());
    assertThat(pushSetupAlarmDto.getRepetitionDelay())
        .isEqualTo(pushSetupAlarm.getRepetitionDelay());
  }

  // method to test CosemObisCode object mapping
  private void checkCosemObisCodeMapping(
      final CosemObisCode cosemObisCode, final CosemObisCodeDto cosemObisCodeDto) {

    // make sure neither is null
    assertThat(cosemObisCode).isNotNull();
    assertThat(cosemObisCodeDto).isNotNull();

    // make sure all instance variables are equal
    assertThat(cosemObisCodeDto.getA()).isEqualTo(cosemObisCode.getA());
    assertThat(cosemObisCodeDto.getB()).isEqualTo(cosemObisCode.getB());
    assertThat(cosemObisCodeDto.getC()).isEqualTo(cosemObisCode.getC());
    assertThat(cosemObisCodeDto.getD()).isEqualTo(cosemObisCode.getD());
    assertThat(cosemObisCodeDto.getE()).isEqualTo(cosemObisCode.getE());
    assertThat(cosemObisCodeDto.getF()).isEqualTo(cosemObisCode.getF());
  }

  // method to test SendDestinationAndMethod mapping
  private void checkSendDestinationAndMethodMapping(
      final PushSetupAlarm pushSetupAlarm, final PushSetupAlarmDto pushSetupAlarmDto) {
    final SendDestinationAndMethod sendDestinationAndMethod =
        pushSetupAlarm.getSendDestinationAndMethod();
    final SendDestinationAndMethodDto sendDestinationAndMethodDto =
        pushSetupAlarmDto.getSendDestinationAndMethod();

    // make sure neither is null
    assertThat(sendDestinationAndMethod).isNotNull();
    assertThat(sendDestinationAndMethodDto).isNotNull();

    // make sure all instance variables are equal
    assertThat(sendDestinationAndMethodDto.getTransportService().name())
        .isEqualTo(sendDestinationAndMethod.getTransportService().name());
    assertThat(sendDestinationAndMethodDto.getMessage().name())
        .isEqualTo(sendDestinationAndMethod.getMessage().name());
    assertThat(sendDestinationAndMethodDto.getDestination())
        .isEqualTo(sendDestinationAndMethod.getDestination());
  }

  // method to test non-empty list mapping
  private void checkNonEmptyListMapping(
      final PushSetupAlarm pushSetupAlarm, final PushSetupAlarmDto pushSetupAlarmDto) {

    // test pushObjectList mapping
    assertThat(pushSetupAlarm.getPushObjectList()).isNotNull();
    assertThat(pushSetupAlarmDto.getPushObjectList()).isNotNull();
    assertThat(pushSetupAlarmDto.getPushObjectList().size())
        .isEqualTo(pushSetupAlarm.getPushObjectList().size());

    final CosemObjectDefinition cosemObjectDefinition = pushSetupAlarm.getPushObjectList().get(0);
    final CosemObjectDefinitionDto cosemObjectDefinitionDto =
        pushSetupAlarmDto.getPushObjectList().get(0);
    assertThat(cosemObjectDefinitionDto.getAttributeIndex())
        .isEqualTo(cosemObjectDefinition.getAttributeIndex());
    assertThat(cosemObjectDefinitionDto.getClassId()).isEqualTo(cosemObjectDefinition.getClassId());
    assertThat(cosemObjectDefinitionDto.getDataIndex())
        .isEqualTo(cosemObjectDefinition.getDataIndex());
    this.checkCosemObisCodeMapping(
        cosemObjectDefinition.getLogicalName(), cosemObjectDefinitionDto.getLogicalName());

    // test communicationWindow mapping
    assertThat(pushSetupAlarm.getCommunicationWindow()).isNotNull();
    assertThat(pushSetupAlarmDto.getCommunicationWindow()).isNotNull();
    assertThat(pushSetupAlarmDto.getCommunicationWindow().size())
        .isEqualTo(pushSetupAlarm.getCommunicationWindow().size());

    final WindowElement windowElement = pushSetupAlarm.getCommunicationWindow().get(0);
    final WindowElementDto windowElementDto = pushSetupAlarmDto.getCommunicationWindow().get(0);

    this.checkCosemDateTimeMapping(windowElement.getStartTime(), windowElementDto.getStartTime());
    this.checkCosemDateTimeMapping(windowElement.getEndTime(), windowElementDto.getEndTime());
  }

  // method to test mapping of CosemDateTime objects
  private void checkCosemDateTimeMapping(
      final CosemDateTime cosemDateTime, final CosemDateTimeDto cosemDateTimeDto) {

    // make sure neither is null
    assertThat(cosemDateTime).isNotNull();
    assertThat(cosemDateTimeDto).isNotNull();

    // check variables
    assertThat(cosemDateTimeDto.getDeviation()).isEqualTo(cosemDateTime.getDeviation());

    final ClockStatus clockStatus = cosemDateTime.getClockStatus();
    final ClockStatusDto clockStatusDto = cosemDateTimeDto.getClockStatus();
    assertThat(clockStatusDto.getStatus()).isEqualTo(clockStatus.getStatus());
    assertThat(clockStatusDto.isSpecified()).isEqualTo(clockStatus.isSpecified());

    final CosemDate cosemDate = cosemDateTime.getDate();
    final CosemDateDto cosemDateDto = cosemDateTimeDto.getDate();
    assertThat(cosemDateDto).isEqualToComparingFieldByField(cosemDate);

    final CosemTime cosemTime = cosemDateTime.getTime();
    final CosemTimeDto cosemTimeDto = cosemDateTimeDto.getTime();
    assertThat(cosemTimeDto).isEqualToComparingFieldByField(cosemTime);
  }
}
