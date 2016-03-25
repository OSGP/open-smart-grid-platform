/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Action;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;
import com.alliander.osgp.shared.exceptionhandling.OsgpException;

@Service(value = "wsSmartMeteringActionMapperService")
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

    private static Map<Class<?>, ConfigurableMapper> classToMapperMap = new HashMap<>();

    /**
     * Specifies which mapper to use for the ws class received.
     */
    @PostConstruct
    private void postConstruct() {
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType.class,
                this.adhocMapper);
        classToMapperMap.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class,
                this.configurationMapper);
        classToMapperMap.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData.class,
                this.configurationMapper);
        classToMapperMap
        .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData.class,
                this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm.class,
                this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms.class,
                this.configurationMapper);
        classToMapperMap.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType.class,
                this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,
                this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,
                this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device.class,
                this.installationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,
                this.managementMapper);
        classToMapperMap.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData.class,
                this.monitoringMapper);
        classToMapperMap.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest.class,
                this.monitoringMapper);
    }

    /**
     * Specifies to which core object the ws object needs to be mapped.
     */
    private static Map<Class<?>, Class<? extends ActionValueObject>> classMap = new HashMap<>();
    static {
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData.class);
        classMap.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest.class);
    }

    public List<ActionValueObject> mapAllActions(final List<? extends Action> actionList) throws OsgpException {
        final List<ActionValueObject> actionValueObjectList = new ArrayList<>();

        for (final Action action : actionList) {

            final ConfigurableMapper mapper = this.getMapper(action);
            final Class<? extends ActionValueObject> clazz = this.getClazz(action);
            final ActionValueObject actionValueObject = this.getActionValueObject(action, mapper, clazz);

            actionValueObjectList.add(actionValueObject);
        }

        return actionValueObjectList;
    }

    private ActionValueObject getActionValueObject(final Action action, final ConfigurableMapper mapper,
            final Class<? extends ActionValueObject> clazz) throws FunctionalException {
        final ActionValueObject actionValueObject = mapper.map(action, clazz);

        if (actionValueObject == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object for Action of class: "
                            + action.getClass().getName()));
        }
        return actionValueObject;
    }

    private Class<? extends ActionValueObject> getClazz(final Action action) throws FunctionalException {
        final Class<? extends ActionValueObject> clazz = classMap.get(action.getClass());

        if (clazz == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object class for Action class: "
                            + action.getClass().getName()));
        }
        return clazz;
    }

    private ConfigurableMapper getMapper(final Action action) throws FunctionalException {
        final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());

        if (mapper == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No mapper for Action class: "
                            + action.getClass().getName()));
        }
        return mapper;
    }
}
