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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGasData;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

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

    private static Map<Class<?>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();

    /**
     * Specifies which mapper to use for the ws class received.
     */
    @PostConstruct
    private void postConstruct() {
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData.class,
                this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,
                this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusData.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm.class,
                this.configurationMapper);

        // ok to here
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType.class,
                this.adhocMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP
                .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData.class,
                        this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,
                this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device.class,
                this.installationMapper);
        CLASS_TO_MAPPER_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestData.class,
                this.monitoringMapper);

        // Requires special mapping from ws object to core object
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery.class
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestGasQuery.class
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData.class,
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData.class,

    }

    /**
     * Specifies to which core object the ws object needs to be mapped.
     */
    private static Map<Class<?>, Class<? extends ActionValueObject>> CLASS_MAP = new HashMap<>();
    static {
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SpecialDaysRequestData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery.class);
        CLASS_MAP.put(
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministrativeStatusData.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);
        // CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData.class,
        // ActualMeterReadsData.class);

        // ok to here
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType.class);
        CLASS_MAP
        .put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequestData.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);
        CLASS_MAP.put(com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device.class,
                com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice.class);

        // Requires special mapping for Gas, so ommit it from the standard
        // mapper
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestGasQuery
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData
        // com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData.class,

    }

    public List<ActionValueObject> mapAllActions(final List<? extends Action> actionList) throws FunctionalException {
        final List<ActionValueObject> actionValueObjectList = new ArrayList<>();

        for (final Action action : actionList) {

            final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
            final Class<? extends ActionValueObject> clazz = CLASS_MAP.get(action.getClass());
            if (mapper != null) {
                actionValueObjectList.add(this.getActionValueObjectWithDefaultMapper(action, mapper, clazz));
            } else {
                actionValueObjectList.add(this.convertWsToCore(action));
            }
        }

        return actionValueObjectList;
    }

    private ActionValueObject convertWsToCore(final Action action) throws FunctionalException {

        if (action instanceof com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData) {
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData actualMeterReadsGasData = (com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasData) action;
            return new ActualMeterReadsGasData(actualMeterReadsGasData.getDeviceIdentification());
        } else if (action instanceof com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsData) {
            return new ActualMeterReadsData();

        } else if (action instanceof com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestGasQuery) {
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestGasQuery periodicReadsRequestGasQuery = (com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestGasQuery) action;
            return new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery(
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(periodicReadsRequestGasQuery
                            .getPeriodicReadsRequestData().getPeriodType().name()), periodicReadsRequestGasQuery
                            .getPeriodicReadsRequestData().getBeginDate().toGregorianCalendar().getTime(),
                    periodicReadsRequestGasQuery.getPeriodicReadsRequestData().getEndDate().toGregorianCalendar()
                            .getTime(), true, periodicReadsRequestGasQuery.getDeviceIdentification());
        } else if (action instanceof com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery) {
            final com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery periodicReadsRequestQuery = (com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequestQuery) action;

            return new com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery(
                    com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodType.valueOf(periodicReadsRequestQuery
                            .getPeriodicReadsRequestData().getPeriodType().name()), periodicReadsRequestQuery
                            .getPeriodicReadsRequestData().getBeginDate().toGregorianCalendar().getTime(),
                    periodicReadsRequestQuery.getPeriodicReadsRequestData().getEndDate().toGregorianCalendar()
                            .getTime());

        } else {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new AssertionError("No mapper defined for class: "
                            + action.getClass().getName()));
        }

    }

    private ActionValueObject getActionValueObjectWithDefaultMapper(final Action action,
            final ConfigurableMapper mapper, final Class<? extends ActionValueObject> clazz) throws FunctionalException {
        final ActionValueObject actionValueObject = mapper.map(action, clazz);

        if (actionValueObject == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object for Action of class: "
                            + action.getClass().getName()));
        }
        return actionValueObject;
    }

    private Class<? extends ActionValueObject> getClazz(final Action action) throws FunctionalException {
        final Class<? extends ActionValueObject> clazz = CLASS_MAP.get(action.getClass());

        if (clazz == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object class for Action class: "
                            + action.getClass().getName()));
        }
        return clazz;
    }

    private ConfigurableMapper getMapper(final Action action) throws FunctionalException {
        final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());

        if (mapper == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No mapper for Action class: "
                            + action.getClass().getName()));
        }
        return mapper;
    }
}
