/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * Helper class to create ActionDto objects.
 *
 */
public class ActionDtoBuilder {

    public FindEventsRequestDataDto makeFindEventsQueryDto() {
        return new FindEventsRequestDataDto(EventLogCategoryDto.STANDARD_EVENT_LOG, new DateTime(), new DateTime());
    }

    public ActualMeterReadsDataDto makeActualMeterReadsDataDtoAction() {
        return new ActualMeterReadsDataDto();
    }

    public GetAdministrativeStatusDataDto makeGetAdministrativeStatusDataDto() {
        return new GetAdministrativeStatusDataDto();
    }

    public SpecialDaysRequestDataDto makeSpecialDaysRequestDataDto() {
        final List<SpecialDayDto> specialDays = new ArrayList<>();
        specialDays.add(new SpecialDayDto(new CosemDateDto(2016, 1, 1), 1));
        final SpecialDaysRequestDataDto result = new SpecialDaysRequestDataDto(specialDays);
        return result;
    }

    public ReadAlarmRegisterDataDto makeReadAlarmRegisterDataDto() {
        return new ReadAlarmRegisterDataDto();
    }

    public PeriodicMeterReadsRequestDataDto makePeriodicMeterReadsRequestDataDto() {
        return new PeriodicMeterReadsRequestDataDto(PeriodTypeDto.DAILY, new Date(), new Date());
    }

    public PeriodicMeterReadsGasRequestDataDto makePeriodicMeterReadsGasRequestDataDto() {
        return new PeriodicMeterReadsGasRequestDataDto(PeriodTypeDto.DAILY, new Date(), new Date(), ChannelDto.ONE);
    }

    public AdministrativeStatusTypeDataDto makeAdministrativeStatusTypeDataDto() {
        return new AdministrativeStatusTypeDataDto(AdministrativeStatusTypeDto.ON);
    }

    public ActivityCalendarDataDto makeActivityCalendarDataDto() {
        final List<SeasonProfileDto> profiles = new ArrayList<>();
        final ActivityCalendarDto activity = new ActivityCalendarDto("calenderName", this.makeCosemDateTimeDto(),
                profiles);
        final ActivityCalendarDataDto result = new ActivityCalendarDataDto(activity);
        return result;
    }

    public GMeterInfoDto makeGMeterInfoDto() {
        return new GMeterInfoDto(1, "EXXXX001692675614");
    }

    public SetAlarmNotificationsRequestDataDto makeSetAlarmNotificationsRequestDataDto() {
        final Set<AlarmNotificationDto> notifications = new HashSet<>();
        final AlarmNotificationsDto notification = new AlarmNotificationsDto(notifications);
        final SetAlarmNotificationsRequestDataDto result = new SetAlarmNotificationsRequestDataDto(notification);
        return result;
    }

    public SetConfigurationObjectRequestDataDto makeSetConfigurationObjectRequestDataDto() {
        final List<ConfigurationFlagDto> flags = new ArrayList<>();
        final ConfigurationFlagsDto configurationFlags = new ConfigurationFlagsDto(flags);
        final ConfigurationObjectDto configuration = new ConfigurationObjectDto(GprsOperationModeTypeDto.ALWAYS_ON,
                configurationFlags);
        final SetConfigurationObjectRequestDataDto result = new SetConfigurationObjectRequestDataDto(configuration);
        return result;
    }

    public SetPushSetupAlarmRequestDataDto makeSetPushSetupAlarmRequestDataDto() {
        final CosemObisCodeDto cosemCode = new CosemObisCodeDto(1, 1, 1, 1, 1, 1);
        final List<CosemObjectDefinitionDto> objectDefinitions = new ArrayList<>();
        final SendDestinationAndMethodDto destinationAndMethod = new SendDestinationAndMethodDto(
                TransportServiceTypeDto.TCP, "destination", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
        final List<WindowElementDto> windowElemenents = new ArrayList<>();
        final PushSetupAlarmDto pushAlarm = new PushSetupAlarmDto(cosemCode, objectDefinitions, destinationAndMethod,
                windowElemenents, 1, 1, 1);
        final SetPushSetupAlarmRequestDataDto result = new SetPushSetupAlarmRequestDataDto(pushAlarm);
        return result;
    }

    public SetPushSetupSmsRequestDataDto mkeSetPushSetupSmsRequestDataDto() {
        final CosemObisCodeDto cosemCode = new CosemObisCodeDto(1, 1, 1, 1, 1, 1);
        final List<CosemObjectDefinitionDto> objectDefinitions = new ArrayList<>();
        final SendDestinationAndMethodDto destinationAndMethod = new SendDestinationAndMethodDto(
                TransportServiceTypeDto.TCP, "destination", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
        final List<WindowElementDto> windowElemenents = new ArrayList<>();
        final PushSetupSmsDto setupSms = new PushSetupSmsDto(cosemCode, objectDefinitions, destinationAndMethod,
                windowElemenents, 1, 1, 1);
        final SetPushSetupSmsRequestDataDto result = new SetPushSetupSmsRequestDataDto(setupSms);
        return result;
    }

    public SynchronizeTimeRequestDataDto makeSynchronizeTimeRequestDataDto() {
        return new SynchronizeTimeRequestDataDto();
    }

    public GetConfigurationRequestDataDto makeGetConfigurationRequestDataDto() {
        return new GetConfigurationRequestDataDto();
    }

    public GetFirmwareVersionRequestDataDto makeGetFirmwareVersionRequestDataDto() {
        return new GetFirmwareVersionRequestDataDto();
    }

    public CosemDateTimeDto makeCosemDateTimeDto() {
        return new CosemDateTimeDto(new DateTime());
    }

}
