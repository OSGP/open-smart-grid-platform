/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.domain.smartmetering.application.services;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmNotificationsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupAlarmDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PushSetupSmsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmartMeteringDeviceDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetailsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;

@Service(value = "wsSmartMeteringActionMapperService")
@Transactional(value = "transactionManager")
@Validated
public class ActionMapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMapperService.class);

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

    private static Map<Class, ConfigurableMapper> classToMapperMap = new HashMap<Class, ConfigurableMapper>();

    // @formatter:off
    /**
     * Specifies whith mapper to use for the ws class received
     */
    @PostConstruct
    private void postConstruct() {
        classToMapperMap.put(SmsDetails.class,                    this.adhocMapper);
        classToMapperMap.put(AdministrativeStatusType.class,      this.configurationMapper);
        classToMapperMap.put(SpecialDaysRequestData.class,        this.configurationMapper);
        classToMapperMap.put(SetConfigurationObjectRequest.class, this.configurationMapper);
        classToMapperMap.put(PushSetupAlarm.class,                this.configurationMapper);
        classToMapperMap.put(PushSetupSms.class,                  this.configurationMapper);
        classToMapperMap.put(ActivityCalendar.class,              this.configurationMapper);
        classToMapperMap.put(AlarmNotifications.class,            this.configurationMapper);
        classToMapperMap.put(KeySet.class,                        this.configurationMapper);
        classToMapperMap.put(SmartMeteringDevice.class,           this.installationMapper);
        classToMapperMap.put(FindEventsQuery.class,               this.managementMapper);
        classToMapperMap.put(PeriodicMeterReadsQuery.class,       this.monitoringMapper);
        classToMapperMap.put(ReadAlarmRegisterRequest.class,      this.monitoringMapper);
    }

    /**
     * Specifies to which core object the ws object needs to be mapped
     */
    private static Map<Class, Class<? extends Serializable>> classMap = new HashMap<Class, Class<? extends Serializable>>();
    static {
        classMap.put(SmsDetails.class,                      SmsDetailsDto.class);
        classMap.put(AdministrativeStatusType.class,        AdministrativeStatusTypeDto.class);
        classMap.put(SpecialDaysRequestData.class,          SpecialDaysRequestDataDto.class);
        classMap.put(SetConfigurationObjectRequest.class,   SetConfigurationObjectRequestDto.class);
        classMap.put(PushSetupAlarm.class,                  PushSetupAlarmDto.class);
        classMap.put(PushSetupSms.class,                    PushSetupSmsDto.class);
        classMap.put(ActivityCalendar.class,                ActivityCalendarDto.class);
        classMap.put(AlarmNotifications.class,              AlarmNotificationsDto.class);
        classMap.put(KeySet.class,                          KeySetDto.class);
        classMap.put(SmartMeteringDevice.class,             SmartMeteringDeviceDto.class);
        classMap.put(FindEventsQuery.class,                 FindEventsQueryDto.class);
        classMap.put(PeriodicMeterReadsQuery.class,         PeriodicMeterReadsDto.class);
        classMap.put(ReadAlarmRegisterRequest.class,        ReadAlarmRegisterRequestDto.class);
    }
    // @formatter:on

    public ActionMapperService() {
        // Parameterless constructor required for transactions
    }

    public BundleMessageDataContainerDto mapAllActions(final BundleMessageDataContainer bundleMessageDataContainer) {

        final List<ActionValueObjectDto> actionValueObjectDtoList = new ArrayList<ActionValueObjectDto>();

        for (final ActionValueObject action : bundleMessageDataContainer.getBundleList()) {

            final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());
            final Class clazz = classMap.get(action.getClass());
            final ActionValueObjectDto actionValueObjectDto = mapper.map(action, clazz);

            actionValueObjectDtoList.add(actionValueObjectDto);
        }

        return new BundleMessageDataContainerDto(actionValueObjectDtoList);
    }
}
