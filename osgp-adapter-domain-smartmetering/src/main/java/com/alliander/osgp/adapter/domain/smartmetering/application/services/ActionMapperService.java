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

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGasData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministrativeStatusData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
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
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
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
    private AdhocMapper adhocMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private InstallationMapper installationMapper;

    @Autowired
    private MonitoringMapper monitoringMapper;

    private static Map<Class<? extends ActionValueObject>, ConfigurableMapper> classToMapperMap = new HashMap<>();

    // @formatter:off
    /**
     * Specifies which mapper to use for the core class received.
     */
    @PostConstruct
    private void postConstruct() {
        classToMapperMap.put(SpecialDaysRequestData.class, this.configurationMapper);
        classToMapperMap.put(ReadAlarmRegisterData.class, this.monitoringMapper);
        classToMapperMap.put(FindEventsQuery.class, this.managementMapper);
        classToMapperMap.put(ActualMeterReadsData.class, this.monitoringMapper);
        classToMapperMap.put(ActualMeterReadsGasData.class, this.monitoringMapper);
        classToMapperMap.put(GetAdministrativeStatusData.class, this.configurationMapper);

        // ok to here
        classToMapperMap.put(SmsDetails.class, this.adhocMapper);
        classToMapperMap.put(AdministrativeStatusType.class, this.configurationMapper);
        classToMapperMap.put(SetConfigurationObjectRequest.class, this.configurationMapper);
        classToMapperMap.put(PushSetupAlarm.class, this.configurationMapper);
        classToMapperMap.put(PushSetupSms.class, this.configurationMapper);
        classToMapperMap.put(ActivityCalendar.class, this.configurationMapper);
        classToMapperMap.put(AlarmNotifications.class, this.configurationMapper);
        classToMapperMap.put(KeySet.class, this.configurationMapper);
        classToMapperMap.put(SmartMeteringDevice.class, this.installationMapper);
        classToMapperMap.put(PeriodicMeterReadsQuery.class, this.monitoringMapper);


    }

    /**
     * Specifies to which DTO object the core object needs to be mapped.
     */
    private static Map<Class<? extends ActionValueObject>, Class<? extends ActionValueObjectDto>> classMap = new HashMap<>();
    static {
        classMap.put(SpecialDaysRequestData.class, SpecialDaysRequestDataDto.class);
        classMap.put(ReadAlarmRegisterData.class, ReadAlarmRegisterRequestDto.class);
        classMap.put(FindEventsQuery.class, FindEventsQueryDto.class);
        classMap.put(ActualMeterReadsData.class, ActualMeterReadsDataDto.class);
        classMap.put(ActualMeterReadsGasData.class, ActualMeterReadsDataGasDto.class);
        classMap.put(GetAdministrativeStatusData.class, GetAdministrativeStatusDataDto.class);
        // ok to here
        classMap.put(SmsDetails.class, SmsDetailsDto.class);
        classMap.put(AdministrativeStatusType.class, AdministrativeStatusTypeDto.class);
        classMap.put(SetConfigurationObjectRequest.class, SetConfigurationObjectRequestDto.class);
        classMap.put(PushSetupAlarm.class, PushSetupAlarmDto.class);
        classMap.put(PushSetupSms.class, PushSetupSmsDto.class);
        classMap.put(ActivityCalendar.class, ActivityCalendarDto.class);
        classMap.put(AlarmNotifications.class, AlarmNotificationsDto.class);
        classMap.put(KeySet.class, KeySetDto.class);
        classMap.put(SmartMeteringDevice.class, SmartMeteringDeviceDto.class);
        classMap.put(PeriodicMeterReadsQuery.class, PeriodicMeterReadsDto.class);

    }

    // @formatter:on

    public BundleMessageDataContainerDto mapAllActions(final BundleMessageDataContainer bundleMessageDataContainer)
            throws FunctionalException {

        final List<ActionValueObjectDto> actionValueObjectDtoList = new ArrayList<ActionValueObjectDto>();

        for (final ActionValueObject action : bundleMessageDataContainer.getBundleList()) {

            final ConfigurableMapper mapper = this.getMapper(action);
            final Class<? extends ActionValueObjectDto> clazz = this.getClazz(action);
            final ActionValueObjectDto actionValueObjectDto = this.getActionValueObjectDto(action, mapper, clazz);

            actionValueObjectDtoList.add(actionValueObjectDto);
        }

        return new BundleMessageDataContainerDto(actionValueObjectDtoList);
    }

    private ActionValueObjectDto getActionValueObjectDto(final ActionValueObject action,
            final ConfigurableMapper mapper, final Class<? extends ActionValueObjectDto> clazz)
                    throws FunctionalException {
        final ActionValueObjectDto actionValueObjectDto = mapper.map(action, clazz);

        if (actionValueObjectDto == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No Action Value Object DTO for Action Value Object of class: "
                                    + action.getClass().getName()));
        }
        return actionValueObjectDto;
    }

    private Class<? extends ActionValueObjectDto> getClazz(final ActionValueObject action) throws FunctionalException {
        final Class<? extends ActionValueObjectDto> clazz = classMap.get(action.getClass());

        if (clazz == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No Action Value Object DTO class for Action Value Object class: "
                                    + action.getClass().getName()));
        }
        return clazz;
    }

    private ConfigurableMapper getMapper(final ActionValueObject action) throws FunctionalException {
        final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());

        if (mapper == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No mapper for Action Value Object class: " + action.getClass().getName()));
        }
        return mapper;
    }
}
