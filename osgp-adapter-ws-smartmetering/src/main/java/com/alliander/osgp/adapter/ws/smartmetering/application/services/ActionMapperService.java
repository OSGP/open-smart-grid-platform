/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

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

import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Action;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionValueObject;

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
    @PostConstruct
    private void postConstruct() {
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType.class,                        this.adhocMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class,      this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest.class,         this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest.class, this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm.class,                this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms.class,                  this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType.class,          this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,            this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,                        this.configurationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device.class,                         this.installationMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,                  this.managementMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest.class,             this.monitoringMapper);
        classToMapperMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest.class,         this.monitoringMapper);
    }

    private static Map<Class, Class<? extends Serializable>> classMap = new HashMap<Class, Class<? extends Serializable>>();
    static {
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.SmsDetailsType.class,                         com.alliander.osgp.domain.core.valueobjects.smartmetering.SmsDetails.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusType.class,       com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusType.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetSpecialDaysRequest.class,          com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequest.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetConfigurationObjectRequest.class,  com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequest.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupAlarm.class,                 com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupAlarm.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.PushSetupSms.class,                   com.alliander.osgp.domain.core.valueobjects.smartmetering.PushSetupSms.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ActivityCalendarType.class,           com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendar.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AlarmNotifications.class,             com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmNotifications.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.KeySet.class,                         com.alliander.osgp.domain.core.valueobjects.smartmetering.KeySet.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.installation.Device.class,                          com.alliander.osgp.domain.core.valueobjects.smartmetering.SmartMeteringDevice.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsQuery.class,                   com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsQuery.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicReadsRequest.class,              com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsQuery.class);
        classMap.put(com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ReadAlarmRegisterRequest.class,          com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterRequest.class);
    }
    // @formatter:on

    public ActionMapperService() {
        // Parameterless constructor required for transactions
    }

    public List<ActionValueObject> mapAllActions(final List<? extends Action> actionList) {
        final List<ActionValueObject> actionValueObjectList = new ArrayList<ActionValueObject>();

        for (final Action action : actionList) {

            final ConfigurableMapper mapper = classToMapperMap.get(action.getClass());
            final Class clazz = classMap.get(action.getClass());
            final ActionValueObject actionValueObject = mapper.map(action, clazz);

            actionValueObjectList.add(actionValueObject);
        }

        return actionValueObjectList;
    }
}
