/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.joda.time.DateTime;

/** Helper class to create ActionDto objects. */
public class ActionDtoBuilder {

  public FindEventsRequestDto makeFindEventsQueryDto() {
    return new FindEventsRequestDto(
        EventLogCategoryDto.STANDARD_EVENT_LOG, new DateTime(), new DateTime());
  }

  public ActualMeterReadsDataDto makeActualMeterReadsDataDtoAction() {
    return new ActualMeterReadsDataDto();
  }

  public ActualPowerQualityRequestDto makeActualPowerQualityRequestDto() {
    return new ActualPowerQualityRequestDto(null);
  }

  public GetAdministrativeStatusDataDto makeGetAdministrativeStatusDataDto() {
    return new GetAdministrativeStatusDataDto();
  }

  public SpecialDaysRequestDataDto makeSpecialDaysRequestDataDto() {
    final List<SpecialDayDto> specialDays = new ArrayList<>();
    specialDays.add(new SpecialDayDto(new CosemDateDto(2016, 1, 1), 1));
    return new SpecialDaysRequestDataDto(specialDays);
  }

  public ReadAlarmRegisterDataDto makeReadAlarmRegisterDataDto() {
    return new ReadAlarmRegisterDataDto();
  }

  public PeriodicMeterReadsRequestDataDto makePeriodicMeterReadsRequestDataDto() {
    return new PeriodicMeterReadsRequestDataDto(PeriodTypeDto.DAILY, new Date(), new Date());
  }

  public PeriodicMeterReadsGasRequestDto makePeriodicMeterReadsGasRequestDataDto() {
    return new PeriodicMeterReadsGasRequestDto(
        PeriodTypeDto.DAILY, new Date(), new Date(), ChannelDto.ONE);
  }

  public AdministrativeStatusTypeDataDto makeAdministrativeStatusTypeDataDto() {
    return new AdministrativeStatusTypeDataDto(AdministrativeStatusTypeDto.ON);
  }

  public ActivityCalendarDataDto makeActivityCalendarDataDto() {
    final List<SeasonProfileDto> profiles = new ArrayList<>();
    final ActivityCalendarDto activity =
        new ActivityCalendarDto("calenderName", this.makeCosemDateTimeDto(), profiles);
    return new ActivityCalendarDataDto(activity);
  }

  public GMeterInfoDto makeGMeterInfoDto() {
    return new GMeterInfoDto(1, "EXXXX001692675614");
  }

  public SetAlarmNotificationsRequestDto makeSetAlarmNotificationsRequestDataDto() {
    final Set<AlarmNotificationDto> notifications = new HashSet<>();
    final AlarmNotificationsDto notification = new AlarmNotificationsDto(notifications);
    return new SetAlarmNotificationsRequestDto(notification);
  }

  public SetConfigurationObjectRequestDataDto makeSetConfigurationObjectRequestDataDto() {
    final List<ConfigurationFlagDto> flags = new ArrayList<>();
    final ConfigurationFlagsDto configurationFlags = new ConfigurationFlagsDto(flags);
    final ConfigurationObjectDto configuration =
        new ConfigurationObjectDto(GprsOperationModeTypeDto.ALWAYS_ON, configurationFlags);
    return new SetConfigurationObjectRequestDataDto(configuration);
  }

  public SetPushSetupAlarmRequestDto makeSetPushSetupAlarmRequestDataDto() {
    final CosemObisCodeDto cosemCode = new CosemObisCodeDto(new int[] {1, 1, 1, 1, 1, 1});
    final List<CosemObjectDefinitionDto> objectDefinitions = new ArrayList<>();
    final SendDestinationAndMethodDto destinationAndMethod =
        new SendDestinationAndMethodDto(
            TransportServiceTypeDto.TCP, "destination", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
    final List<WindowElementDto> windowElemenents = new ArrayList<>();

    final PushSetupAlarmDto.Builder pushSetupAlarmBuilder = new PushSetupAlarmDto.Builder();
    pushSetupAlarmBuilder
        .withLogicalName(cosemCode)
        .withPushObjectList(objectDefinitions)
        .withSendDestinationAndMethod(destinationAndMethod)
        .withCommunicationWindow(windowElemenents)
        .withRandomisationStartInterval(1)
        .withNumberOfRetries(1)
        .withRepetitionDelay(1);
    final PushSetupAlarmDto pushAlarm = pushSetupAlarmBuilder.build();

    return new SetPushSetupAlarmRequestDto(pushAlarm);
  }

  public SetPushSetupLastGaspRequestDto makeSetPushSetupLastGaspRequestDataDto() {
    final CosemObisCodeDto cosemCode = new CosemObisCodeDto(new int[] {1, 1, 1, 1, 1, 1});
    final List<CosemObjectDefinitionDto> objectDefinitions = new ArrayList<>();
    final SendDestinationAndMethodDto destinationAndMethod =
        new SendDestinationAndMethodDto(
            TransportServiceTypeDto.UDP, "destination", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
    final List<WindowElementDto> windowElemenents = new ArrayList<>();

    final PushSetupLastGaspDto.Builder pushSetupLastGaspBuilder =
        new PushSetupLastGaspDto.Builder();
    pushSetupLastGaspBuilder
        .withLogicalName(cosemCode)
        .withPushObjectList(objectDefinitions)
        .withSendDestinationAndMethod(destinationAndMethod)
        .withCommunicationWindow(windowElemenents)
        .withRandomisationStartInterval(1)
        .withNumberOfRetries(1)
        .withRepetitionDelay(1);
    final PushSetupLastGaspDto setupLastGasp = pushSetupLastGaspBuilder.build();

    return new SetPushSetupLastGaspRequestDto(setupLastGasp);
  }

  public SetPushSetupSmsRequestDto makeSetPushSetupSmsRequestDataDto() {
    final CosemObisCodeDto cosemCode = new CosemObisCodeDto(new int[] {1, 1, 1, 1, 1, 1});
    final List<CosemObjectDefinitionDto> objectDefinitions = new ArrayList<>();
    final SendDestinationAndMethodDto destinationAndMethod =
        new SendDestinationAndMethodDto(
            TransportServiceTypeDto.TCP, "destination", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
    final List<WindowElementDto> windowElemenents = new ArrayList<>();

    final PushSetupSmsDto.Builder pushSetupSmsBuilder = new PushSetupSmsDto.Builder();
    pushSetupSmsBuilder
        .withLogicalName(cosemCode)
        .withPushObjectList(objectDefinitions)
        .withSendDestinationAndMethod(destinationAndMethod)
        .withCommunicationWindow(windowElemenents)
        .withRandomisationStartInterval(1)
        .withNumberOfRetries(1)
        .withRepetitionDelay(1);
    final PushSetupSmsDto setupSms = pushSetupSmsBuilder.build();

    return new SetPushSetupSmsRequestDto(setupSms);
  }

  public SynchronizeTimeRequestDto makeSynchronizeTimeRequestDataDto() {
    return new SynchronizeTimeRequestDto("Europe/Amsterdam");
  }

  public GetAllAttributeValuesRequestDto makeGetAllAttributeValuesRequestDto() {
    return new GetAllAttributeValuesRequestDto();
  }

  public GetFirmwareVersionRequestDto makeGetFirmwareVersionRequestDataDto() {
    return new GetFirmwareVersionRequestDto();
  }

  public CosemDateTimeDto makeCosemDateTimeDto() {
    return new CosemDateTimeDto(new DateTime());
  }
}
