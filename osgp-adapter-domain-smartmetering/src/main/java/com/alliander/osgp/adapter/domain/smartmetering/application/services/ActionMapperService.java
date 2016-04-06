/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ActualMeterReadsRequestGasRequestDataConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.CommonMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.CustomValueToDtoConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.PeriodicReadsRequestGasDataConverter;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGasRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministrativeStatusData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGasRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetailsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "domainSmartMeteringActionMapperService")
@Validated
public class ActionMapperService {

    @Autowired
    private ManagementMapper managementMapper;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private DomainHelperService domainHelperService;

    @Autowired
    private PeriodicReadsRequestGasDataConverter periodicReadsRequestGasDataConverter;

    @Autowired
    private ActualMeterReadsRequestGasRequestDataConverter actualReadsRequestGasDataConverter;

    private static Map<Class<? extends ActionValueObject>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();
    private static Map<Class<? extends ActionValueObject>, CustomValueToDtoConverter<? extends ActionValueObject, ? extends ActionValueObjectDto>> SPECIAL_CONVERTER_FOR_CLASS = new HashMap<>();

    /**
     * Specifies which mapper to use for the core class received.
     */
    @PostConstruct
    private void postConstruct() {

        SPECIAL_CONVERTER_FOR_CLASS.put(PeriodicMeterReadsGasRequestData.class,
                this.periodicReadsRequestGasDataConverter);
        SPECIAL_CONVERTER_FOR_CLASS.put(ActualMeterReadsGasRequestData.class, this.actualReadsRequestGasDataConverter);

        CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsRequestData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(ActualMeterReadsRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(SpecialDaysRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(ReadAlarmRegisterData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(FindEventsQuery.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(GetAdministrativeStatusData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(PushSetupAlarm.class, this.commonMapper);

        // ok to here
        CLASS_TO_MAPPER_MAP.put(AdministrativeStatusType.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(SetConfigurationObjectRequest.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(PushSetupSms.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(ActivityCalendar.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(AlarmNotifications.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(KeySet.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(SmartMeteringDevice.class, this.commonMapper);
    }

    /**
     * Specifies to which DTO object the core object needs to be mapped.
     */
    private static Map<Class<? extends ActionValueObject>, Class<? extends ActionValueObjectDto>> CLASS_MAP = new HashMap<>();
    static {
        CLASS_MAP.put(PeriodicMeterReadsRequestData.class, PeriodicMeterReadsRequestDataDto.class);
        CLASS_MAP.put(ActualMeterReadsRequestData.class, ActualMeterReadsDataDto.class);
        CLASS_MAP.put(SpecialDaysRequestData.class, SpecialDaysRequestDataDto.class);
        CLASS_MAP.put(ReadAlarmRegisterData.class, ReadAlarmRegisterDataDto.class);
        CLASS_MAP.put(FindEventsQuery.class, FindEventsQueryDto.class);
        CLASS_MAP.put(GetAdministrativeStatusData.class, GetAdministrativeStatusDataDto.class);
        CLASS_MAP.put(PushSetupAlarm.class, PushSetupAlarmDto.class);

        // ok to here
        CLASS_MAP.put(SmsDetails.class, SmsDetailsDto.class);
        CLASS_MAP.put(AdministrativeStatusType.class, AdministrativeStatusTypeDto.class);
        CLASS_MAP.put(SetConfigurationObjectRequest.class, SetConfigurationObjectRequestDto.class);
        CLASS_MAP.put(PushSetupSms.class, PushSetupSmsDto.class);
        CLASS_MAP.put(ActivityCalendar.class, ActivityCalendarDto.class);
        CLASS_MAP.put(AlarmNotifications.class, AlarmNotificationsDto.class);
        CLASS_MAP.put(KeySet.class, KeySetDto.class);
        CLASS_MAP.put(SmartMeteringDevice.class, SmartMeteringDeviceDto.class);

    }

    public BundleMessageDataContainerDto mapAllActions(final BundleMessageDataContainer bundleMessageDataContainer,
            final SmartMeter smartMeter) throws FunctionalException {

        final List<ActionValueObjectDto> actionValueObjectDtoList = new ArrayList<ActionValueObjectDto>();

        for (final ActionValueObject action : bundleMessageDataContainer.getBundleList()) {

            @SuppressWarnings("unchecked")
            // suppress else the compiler will complain
            final CustomValueToDtoConverter<ActionValueObject, ActionValueObjectDto> customValueToDtoConverter = (CustomValueToDtoConverter<ActionValueObject, ActionValueObjectDto>) SPECIAL_CONVERTER_FOR_CLASS
                    .get(action.getClass());

            if (customValueToDtoConverter != null) {
                actionValueObjectDtoList.add(customValueToDtoConverter.convert(action, smartMeter));
            } else {

                final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
                final Class<? extends ActionValueObjectDto> clazz = CLASS_MAP.get(action.getClass());
                if (mapper != null) {
                    actionValueObjectDtoList.add(this.performDefaultMapping(action, mapper, clazz));
                } else {
                    throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.DOMAIN_SMART_METERING, new AssertionError("No mapper defined for class: "
                                    + clazz.getName()));
                }
            }
        }
        return new BundleMessageDataContainerDto(actionValueObjectDtoList);
    }

    private ActionValueObjectDto performDefaultMapping(final ActionValueObject action, final ConfigurableMapper mapper,
            final Class<? extends ActionValueObjectDto> clazz) throws FunctionalException {
        final ActionValueObjectDto actionValueObjectDto = mapper.map(action, clazz);

        if (actionValueObjectDto == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException("Object: " + action.getClass().getName()
                            + " could not be converted to " + clazz.getName()));
        }
        return actionValueObjectDto;
    }

}
