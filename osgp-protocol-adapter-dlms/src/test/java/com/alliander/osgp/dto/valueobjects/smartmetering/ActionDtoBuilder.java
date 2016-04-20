package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

/**
 * Helper class to create ActionDto objects.
 * @author dev
 *
 */
public class ActionDtoBuilder {

    public FindEventsQueryDto makeFindEventsQueryDto() {
        FindEventsQueryDto result = new FindEventsQueryDto(EventLogCategoryDto.STANDARD_EVENT_LOG, new DateTime(),
                new DateTime());
        return result;
    }

    public ActualMeterReadsDataDto makeActualMeterReadsDataDtoAction() {
        ActualMeterReadsDataDto result = new ActualMeterReadsDataDto();
        return result;
    }

    public GetAdministrativeStatusDataDto makeGetAdministrativeStatusDataDto() {
        final GetAdministrativeStatusDataDto result = new GetAdministrativeStatusDataDto();
        return result;
    }

    public SpecialDaysRequestDataDto makeSpecialDaysRequestDataDto() {
        final List<SpecialDayDto> specialDays = new ArrayList<>();
        specialDays.add(new SpecialDayDto(new CosemDateDto(2016, 1, 1), 1));
        SpecialDaysRequestDataDto result = new SpecialDaysRequestDataDto(specialDays);
        return result;
    }

    public ReadAlarmRegisterDataDto makeReadAlarmRegisterDataDto() {
        final ReadAlarmRegisterDataDto result = new ReadAlarmRegisterDataDto();
        return result;
    }

    public PeriodicMeterReadsRequestDataDto makePeriodicMeterReadsRequestDataDto() {
        PeriodicMeterReadsRequestDataDto result = new PeriodicMeterReadsRequestDataDto(PeriodTypeDto.DAILY, new Date(),
                new Date());
        return result;
    }

    public PeriodicMeterReadsGasRequestDataDto makePeriodicMeterReadsGasRequestDataDto() {
        PeriodicMeterReadsGasRequestDataDto result = new PeriodicMeterReadsGasRequestDataDto(PeriodTypeDto.DAILY,
                new Date(), new Date(), ChannelDto.ONE);
        return result;
    }

    public AdministrativeStatusTypeDataDto makeAdministrativeStatusTypeDataDto() {
        final AdministrativeStatusTypeDataDto result = new AdministrativeStatusTypeDataDto(
                AdministrativeStatusTypeDto.ON);
        return result;
    }

    public ActivityCalendarDataDto makeActivityCalendarDataDto() {
        final List<SeasonProfileDto> profiles = new ArrayList<>();
        final ActivityCalendarDto activity = new ActivityCalendarDto("todo", makeCosemDateTimeDto(), profiles);
        final ActivityCalendarDataDto result = new ActivityCalendarDataDto(activity);
        return result;
    }
    
    public GMeterInfoDto makeGMeterInfoDto() {
        final GMeterInfoDto result = new GMeterInfoDto(1, "todo");
        return result;
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
                TransportServiceTypeDto.TCP, "todo", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
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
                TransportServiceTypeDto.TCP, "todo", MessageTypeDto.XML_ENCODED_X_DLMS_APDU);
        final List<WindowElementDto> windowElemenents = new ArrayList<>();
        final PushSetupSmsDto setupSms = new PushSetupSmsDto(cosemCode, objectDefinitions, destinationAndMethod,
                windowElemenents, 1, 1, 1);
        final SetPushSetupSmsRequestDataDto result = new SetPushSetupSmsRequestDataDto(setupSms);
        return result;
    }

    public SynchronizeTimeRequestDataDto makeSynchronizeTimeRequestDataDto() {
        final SynchronizeTimeRequestDataDto result = new SynchronizeTimeRequestDataDto();
        return result;
    }

    public GetConfigurationRequestDataDto makeGetConfigurationRequestDataDto() {
        final GetConfigurationRequestDataDto result = new GetConfigurationRequestDataDto();
        return result;
    }

    public GetFirmwareVersionRequestDataDto makeGetFirmwareVersionRequestDataDto() {
        final GetFirmwareVersionRequestDataDto result = new GetFirmwareVersionRequestDataDto();
        return result;
    }
    
    public CosemDateTimeDto makeCosemDateTimeDto() {
        return new CosemDateTimeDto(new DateTime());
    }


}
