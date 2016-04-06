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

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.CommonMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueResponseObject;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleResponseMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionValueObjectResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeResponseDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AlarmRegisterDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleResponseMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.EventMessageDataContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.MeterReadsGasDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsContainerGasDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "domainSmartMeteringActionMapperResponseService")
@Validated
public class ActionMapperResponseService {

    @Autowired
    private ManagementMapper managementMapper;

    @Autowired
    private ConfigurationMapper configurationMapper;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private CommonMapper commonMapper;

    /**
     * Specifies which mapper to use for the DTO class received.
     */
    private static Map<Class<? extends ActionValueObjectResponseDto>, ConfigurableMapper> classToMapperMap = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        /*-
         classToMapperMap.put(SmsDetailsDto.class, this.adhocMapper);
         classToMapperMap.put(AdministrativeStatusTypeDto.class, this.configurationMapper);
         classToMapperMap.put(SpecialDaysRequestData.class, this.configurationMapper);
         classToMapperMap.put(SetConfigurationObjectRequest.class, this.configurationMapper);
         classToMapperMap.put(PushSetupAlarm.class, this.configurationMapper);
         classToMapperMap.put(PushSetupSms.class, this.configurationMapper);
         classToMapperMap.put(ActivityCalendar.class, this.configurationMapper);
         classToMapperMap.put(AlarmNotifications.class, this.configurationMapper);
         classToMapperMap.put(KeySet.class, this.configurationMapper);
         classToMapperMap.put(SmartMeteringDevice.class, this.installationMapper);
         classToMapperMap.put(PeriodicMeterReadsContainerGasDto.class, this.monitoringMapper);

         */
        classToMapperMap.put(EventMessageDataContainerDto.class, this.managementMapper);
        classToMapperMap.put(MeterReadsDto.class, this.monitoringMapper);
        classToMapperMap.put(MeterReadsGasDto.class, this.monitoringMapper);
        classToMapperMap.put(ActionValueObjectResponseDto.class, this.commonMapper);
        classToMapperMap.put(AlarmRegisterDto.class, this.commonMapper);
        classToMapperMap.put(AdministrativeStatusTypeResponseDto.class, this.configurationMapper);
        classToMapperMap.put(PeriodicMeterReadsContainerDto.class, this.monitoringMapper);
        classToMapperMap.put(PeriodicMeterReadsContainerGasDto.class, this.monitoringMapper);
    }

    /**
     * Specifies to which core value object the DTO object needs to be mapped.
     */
    private static Map<Class<? extends ActionValueObjectResponseDto>, Class<? extends ActionValueResponseObject>> classMap = new HashMap<>();
    static {
        /*-
         classMap.put(SmsDetailsDto.class, SmsDetails.class);
         classMap.put(AdministrativeStatusTypeDto.class, AdministrativeStatusType.class);
         classMap.put(SpecialDaysRequestData.class, SpecialDaysRequestDataDto.class);
         classMap.put(SetConfigurationObjectRequest.class, SetConfigurationObjectRequestDto.class);
         classMap.put(PushSetupAlarm.class, PushSetupAlarmDto.class);
         classMap.put(PushSetupSms.class, PushSetupSmsDto.class);
         classMap.put(ActivityCalendar.class, ActivityCalendarDto.class);
         classMap.put(AlarmNotifications.class, AlarmNotificationsDto.class);
         classMap.put(KeySet.class, KeySetDto.class);
         classMap.put(SmartMeteringDevice.class, SmartMeteringDeviceDto.class);
         classMap.put(PeriodicMeterReadsContainerGasDto.class, PeriodicMeterReadsContainerGas.class);

         */
        classMap.put(EventMessageDataContainerDto.class, EventMessageDataContainer.class);
        classMap.put(MeterReadsDto.class, MeterReads.class);
        classMap.put(MeterReadsGasDto.class, MeterReadsGas.class);
        classMap.put(ActionValueObjectResponseDto.class, ActionValueResponseObject.class);
        classMap.put(AlarmRegisterDto.class, AlarmRegister.class);
        classMap.put(AdministrativeStatusTypeResponseDto.class, AdministrativeStatusTypeResponse.class);
        classMap.put(PeriodicMeterReadsContainerDto.class, PeriodicMeterReadsContainer.class);
        classMap.put(PeriodicMeterReadsContainerGasDto.class, PeriodicMeterReadsContainerGas.class);

    }

    public BundleResponseMessageDataContainer mapAllActions(
            final BundleResponseMessageDataContainerDto bundleResponseMessageDataContainerDto)
                    throws FunctionalException {

        final List<ActionValueResponseObject> actionResponseList = new ArrayList<ActionValueResponseObject>();

        for (final ActionValueObjectResponseDto action : bundleResponseMessageDataContainerDto
                .getActionValueObjectResponseDto()) {

            final ConfigurableMapper mapper = this.getMapper(action);
            final Class<? extends ActionValueResponseObject> clazz = this.getClazz(action);
            final ActionValueResponseObject actionValueResponseObject = this.doMap(action, mapper, clazz);

            actionResponseList.add(actionValueResponseObject);
        }

        return new BundleResponseMessageDataContainer(actionResponseList);

    }

    private ActionValueResponseObject doMap(final ActionValueObjectResponseDto action, final ConfigurableMapper mapper,
            final Class<? extends ActionValueResponseObject> clazz) throws FunctionalException {
        final ActionValueResponseObject actionValueResponseObject = mapper.map(action, clazz);

        if (actionValueResponseObject == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No Action Value Response Object for Action Value Response DTO Object of class: "
                                    + action.getClass().getName()));
        }
        return actionValueResponseObject;
    }

    private Class<? extends ActionValueResponseObject> getClazz(final ActionValueObjectResponseDto action)
            throws FunctionalException {
        final Class<? extends ActionValueResponseObject> clazz = classMap.get(action.getClass());

        if (clazz == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No Action Value Response Object class for Action Value Response DTO Object class: "
                                    + action.getClass().getName()));
        }
        return clazz;
    }

    private ConfigurableMapper getMapper(final ActionValueObjectResponseDto action) throws FunctionalException {
        final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());

        if (mapper == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No mapper for Action Value Response DTO Object class: " + action.getClass().getName()));
        }
        return mapper;
    }
}
