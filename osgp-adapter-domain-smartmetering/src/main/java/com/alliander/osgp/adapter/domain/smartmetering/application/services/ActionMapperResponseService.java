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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueResponseObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleResponseMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleResponseMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SmsDetailsDto;

@Service(value = "wsSmartMeteringActionMapperResponseService")
@Transactional(value = "transactionManager")
@Validated
public class ActionMapperResponseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionMapperResponseService.class);

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
        classToMapperMap.put(SmsDetailsDto.class,                       this.adhocMapper);
        classToMapperMap.put(AdministrativeStatusTypeDto.class,         this.configurationMapper);
        //        classToMapperMap.put(SpecialDaysRequestData.class,        this.configurationMapper);
        //        classToMapperMap.put(SetConfigurationObjectRequest.class, this.configurationMapper);
        //        classToMapperMap.put(PushSetupAlarm.class,                this.configurationMapper);
        //        classToMapperMap.put(PushSetupSms.class,                  this.configurationMapper);
        //        classToMapperMap.put(ActivityCalendar.class,              this.configurationMapper);
        //        classToMapperMap.put(AlarmNotifications.class,            this.configurationMapper);
        //        classToMapperMap.put(KeySet.class,                        this.configurationMapper);
        //        classToMapperMap.put(SmartMeteringDevice.class,           this.installationMapper);
        classToMapperMap.put(EventMessageDataContainerDto.class,               this.managementMapper);
        classToMapperMap.put(PeriodicMeterReadsContainerGasDto.class,       this.monitoringMapper);
        classToMapperMap.put(PeriodicMeterReadsContainerDto.class,       this.monitoringMapper);
        classToMapperMap.put(MeterReadsDto.class,                       this.monitoringMapper);
        classToMapperMap.put(MeterReadsGasDto.class,                       this.monitoringMapper);
        classToMapperMap.put(AlarmRegisterDto.class,                     this.monitoringMapper);
    }

    /**
     * Specifies to which core object the ws object needs to be mapped
     */
    private static Map<Class, Class<? extends Serializable>> classMap = new HashMap<Class, Class<? extends Serializable>>();
    static {
        classMap.put(SmsDetailsDto.class,                         SmsDetails.class);
        classMap.put(AdministrativeStatusTypeDto.class,             AdministrativeStatusType.class);
        //        classMap.put(SpecialDaysRequestData.class,          SpecialDaysRequestDataDto.class);
        //        classMap.put(SetConfigurationObjectRequest.class,   SetConfigurationObjectRequestDto.class);
        //        classMap.put(PushSetupAlarm.class,                  PushSetupAlarmDto.class);
        //        classMap.put(PushSetupSms.class,                    PushSetupSmsDto.class);
        //        classMap.put(ActivityCalendar.class,                ActivityCalendarDto.class);
        //        classMap.put(AlarmNotifications.class,              AlarmNotificationsDto.class);
        //        classMap.put(KeySet.class,                          KeySetDto.class);
        //        classMap.put(SmartMeteringDevice.class,             SmartMeteringDeviceDto.class);
        classMap.put(EventMessageDataContainerDto.class,                 EventMessageDataContainer.class);
        classMap.put(PeriodicMeterReadsContainerGasDto.class,         PeriodicMeterReadsContainerGas.class);
        classMap.put(PeriodicMeterReadsContainerDto.class,         PeriodicMeterReadContainer.class);
        classMap.put(MeterReadsDto.class,                          MeterReads.class);
        classMap.put(MeterReadsGasDto.class,                          MeterReadsGas.class);
        classMap.put(AlarmRegisterDto.class,                         AlarmRegister.class);
    }
    // @formatter:on

    public ActionMapperResponseService() {
        // Parameterless constructor required for transactions
    }

    public BundleResponseMessageDataContainer mapAllActions(
            final BundleResponseMessageDataContainerDto bundleResponseMessageDataContainerDto) {

        final List<ActionValueResponseObject> actionResponseList = new ArrayList<ActionValueResponseObject>();

        for (final ActionValueObjectResponseDto action : bundleResponseMessageDataContainerDto
                .getActionValueObjectResponseDto()) {

            final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());
            final Class clazz = classMap.get(action.getClass());
            final ActionValueResponseObject actionValueResponseObjectDto = mapper.map(action, clazz);

            actionResponseList.add(actionValueResponseObjectDto);

        }

        return new BundleResponseMessageDataContainer(actionResponseList);

    }
}
