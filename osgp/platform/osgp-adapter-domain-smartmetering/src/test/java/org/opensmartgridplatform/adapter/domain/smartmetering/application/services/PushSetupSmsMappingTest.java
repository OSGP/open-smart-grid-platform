// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.PushSetupSms;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.SendDestinationAndMethod;
import org.opensmartgridplatform.domain.core.valueobjects.smartmetering.WindowElement;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.ClockStatusDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemDateTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObisCodeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemObjectDefinitionDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.CosemTimeDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.PushSetupSmsDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.SendDestinationAndMethodDto;
import org.opensmartgridplatform.dto.valueobjects.smartmetering.WindowElementDto;

public class PushSetupSmsMappingTest {

  private final ConfigurationMapper configurationMapper = new ConfigurationMapper();

  // To test if a PushSetupAlarm can be mapped if instance variables are
  // null.
  @Test
  public void testPushSetupSmsMappingNull() {

    // build test data
    final PushSetupSms pushSetupSms = new PushSetupSmsBuilder().withNullValues().build();

    // actual mapping
    final PushSetupSmsDto pushSetupSmsDto =
        this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

    // check values
    assertThat(pushSetupSmsDto).isNotNull();
    assertThat(pushSetupSmsDto.getLogicalName()).isNull();
    assertThat(pushSetupSmsDto.getPushObjectList()).isNull();
    assertThat(pushSetupSmsDto.getSendDestinationAndMethod()).isNull();
    assertThat(pushSetupSmsDto.getCommunicationWindow()).isNull();
    assertThat(pushSetupSmsDto.getRandomisationStartInterval()).isNull();
    assertThat(pushSetupSmsDto.getNumberOfRetries()).isNull();
    assertThat(pushSetupSmsDto.getRepetitionDelay()).isNull();
  }

  // To test if a PushSetupAlarm can be mapped if instance variables are
  // initialized and lists are empty.
  @Test
  public void testPushSetupSmsMappingWithEmptyLists() {

    // build test data
    final ArrayList<CosemObjectDefinition> pushObjectList = new ArrayList<>();
    final ArrayList<WindowElement> communicationWindow = new ArrayList<>();

    final PushSetupSms pushSetupSms =
        new PushSetupSmsBuilder().withEmptyLists(pushObjectList, communicationWindow).build();

    // actual mapping
    final PushSetupSmsDto pushSetupSmsDto =
        this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

    // check values
    this.checkCosemObisCodeMapping(pushSetupSms.getLogicalName(), pushSetupSmsDto.getLogicalName());
    this.checkSendDestinationAndMethodMapping(pushSetupSms, pushSetupSmsDto);
    this.checkIntegerMapping(pushSetupSms, pushSetupSmsDto);

    assertThat(pushSetupSmsDto.getPushObjectList()).isNotNull();
    assertThat(pushSetupSmsDto.getCommunicationWindow()).isNotNull();
  }

  // To test Mapping if lists contain values
  @Test
  public void testPushSetupSmsMappingWithLists() {

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

    final PushSetupSms pushSetupSms =
        new PushSetupSmsBuilder().withFilledLists(cosemObjectDefinition, windowElement).build();

    // actual mapping
    final PushSetupSmsDto pushSetupSmsDto =
        this.configurationMapper.map(pushSetupSms, PushSetupSmsDto.class);

    // check values
    this.checkCosemObisCodeMapping(pushSetupSms.getLogicalName(), pushSetupSmsDto.getLogicalName());
    this.checkSendDestinationAndMethodMapping(pushSetupSms, pushSetupSmsDto);
    this.checkIntegerMapping(pushSetupSms, pushSetupSmsDto);
    this.checkNonEmptyListMapping(pushSetupSms, pushSetupSmsDto);
  }

  // method to test Integer object mapping
  private void checkIntegerMapping(
      final PushSetupSms pushSetupSms, final PushSetupSmsDto pushSetupSmsDto) {

    // make sure none is null
    assertThat(pushSetupSmsDto.getRandomisationStartInterval()).isNotNull();
    assertThat(pushSetupSmsDto.getNumberOfRetries()).isNotNull();
    assertThat(pushSetupSmsDto.getRepetitionDelay()).isNotNull();

    // make sure all values are equal
    assertThat(pushSetupSmsDto.getRandomisationStartInterval())
        .isEqualTo(pushSetupSms.getRandomisationStartInterval());
    assertThat(pushSetupSmsDto.getNumberOfRetries()).isEqualTo(pushSetupSms.getNumberOfRetries());
    assertThat(pushSetupSmsDto.getRepetitionDelay()).isEqualTo(pushSetupSms.getRepetitionDelay());
  }

  // method to test CosemObisCode object mapping
  private void checkCosemObisCodeMapping(
      final CosemObisCode cosemObisCode, final CosemObisCodeDto cosemObisCodeDto) {

    // make sure neither is null
    assertThat(cosemObisCode).isNotNull();
    assertThat(cosemObisCodeDto).isNotNull();

    // make sure all instance variables are equal
    assertThat(cosemObisCodeDto).isEqualToComparingFieldByField(cosemObisCode);
  }

  // method to test SendDestinationAndMethod mapping
  private void checkSendDestinationAndMethodMapping(
      final PushSetupSms pushSetupSms, final PushSetupSmsDto pushSetupSmsDto) {
    final SendDestinationAndMethod sendDestinationAndMethod =
        pushSetupSms.getSendDestinationAndMethod();
    final SendDestinationAndMethodDto sendDestinationAndMethodDto =
        pushSetupSmsDto.getSendDestinationAndMethod();

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
      final PushSetupSms pushSetupSms, final PushSetupSmsDto pushSetupSmsDto) {

    // test pushObjectList mapping
    assertThat(pushSetupSms.getPushObjectList()).isNotNull();
    assertThat(pushSetupSmsDto.getPushObjectList()).isNotNull();
    assertThat(pushSetupSmsDto.getPushObjectList().size())
        .isEqualTo(pushSetupSms.getPushObjectList().size());

    final CosemObjectDefinition cosemObjectDefinition = pushSetupSms.getPushObjectList().get(0);
    final CosemObjectDefinitionDto cosemObjectDefinitionDto =
        pushSetupSmsDto.getPushObjectList().get(0);
    assertThat(cosemObjectDefinitionDto.getAttributeIndex())
        .isEqualTo(cosemObjectDefinition.getAttributeIndex());
    assertThat(cosemObjectDefinitionDto.getClassId()).isEqualTo(cosemObjectDefinition.getClassId());
    assertThat(cosemObjectDefinitionDto.getDataIndex())
        .isEqualTo(cosemObjectDefinition.getDataIndex());

    this.checkCosemObisCodeMapping(
        cosemObjectDefinition.getLogicalName(), cosemObjectDefinitionDto.getLogicalName());

    // test communicationWindow mapping
    assertThat(pushSetupSms.getCommunicationWindow()).isNotNull();
    assertThat(pushSetupSmsDto.getCommunicationWindow()).isNotNull();
    assertThat(pushSetupSmsDto.getCommunicationWindow().size())
        .isEqualTo(pushSetupSms.getCommunicationWindow().size());

    final WindowElement windowElement = pushSetupSms.getCommunicationWindow().get(0);
    final WindowElementDto windowElementDto = pushSetupSmsDto.getCommunicationWindow().get(0);

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
