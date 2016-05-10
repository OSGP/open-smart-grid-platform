/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.application.services;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import ma.glasnost.orika.impl.ConfigurableMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AllResponses;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ObjectFactory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.AdhocMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.CommonMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.InstallationMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.ws.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AlarmRegister;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AssociationLnObjectsResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleResponseMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessageDataContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersionResponseContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationResponseContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

@Service(value = "wsSmartMeteringActionResponseMapperService")
@Validated
public class ActionMapperResponseService {

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

    @Autowired
    private CommonMapper commonMapper;

    private static Map<Class<? extends ActionResponse>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();

    /**
     * Specifies which mapper to use for the core object class received.
     */
    @PostConstruct
    private void postConstruct() {
        CLASS_TO_MAPPER_MAP.put(MeterReads.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(MeterReadsGas.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(EventMessageDataContainer.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(ActionResponse.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(AlarmRegister.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(AdministrativeStatusTypeResponse.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsContainer.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsContainerGas.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(GetConfigurationResponseContainer.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(FirmwareVersionResponseContainer.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(AssociationLnObjectsResponseData.class, this.adhocMapper);
    }

    /**
     * Specifies to which core object the ws object needs to be mapped.
     */
    private static Map<Class<? extends ActionResponse>, Class<?>> CLASS_MAP = new HashMap<>();
    static {
        CLASS_MAP.put(MeterReadsGas.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsGasResponseData.class);
        CLASS_MAP.put(MeterReads.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ActualMeterReadsResponseData.class);
        CLASS_MAP.put(EventMessageDataContainer.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.management.FindEventsResponseData.class);
        CLASS_MAP.put(ActionResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.common.ActionResponseData.class);
        CLASS_MAP.put(AlarmRegister.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.AlarmRegister.class);
        CLASS_MAP
        .put(AdministrativeStatusTypeResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.AdministrativeStatusResponseData.class);
        CLASS_MAP.put(PeriodicMeterReadsContainer.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsResponseData.class);
        CLASS_MAP.put(PeriodicMeterReadsContainerGas.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsGasResponseData.class);
        CLASS_MAP.put(GetConfigurationResponseContainer.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.GetConfigurationResponseData.class);
        CLASS_MAP.put(FirmwareVersionResponseContainer.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetFirmwareVersionResponseData.class);
        CLASS_MAP.put(AssociationLnObjectsResponseData.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.adhoc.AssociationLnObjectsResponseData.class);

    }

    public BundleResponse mapAllActions(final Serializable actionList) throws FunctionalException {

        final BundleResponseMessageDataContainer bundleResponseMessageDataContainer = (BundleResponseMessageDataContainer) actionList;
        final AllResponses allResponses = new ObjectFactory().createAllResponses();
        final List<? extends ActionResponse> actionValueList = bundleResponseMessageDataContainer.getBundleList();

        for (final ActionResponse actionValueResponseObject : actionValueList) {

            final ConfigurableMapper mapper = this.getMapper(actionValueResponseObject);
            final Class<?> clazz = this.getClazz(actionValueResponseObject);
            final Response response = this.doMap(actionValueResponseObject, mapper, clazz);

            allResponses.getResponseList().add(response);

        }

        final BundleResponse bundleResponse = new ObjectFactory().createBundleResponse();
        bundleResponse.setAllResponses(allResponses);
        return bundleResponse;
    }

    private Response doMap(final ActionResponse actionValueResponseObject, final ConfigurableMapper mapper,
            final Class<?> clazz) throws FunctionalException {
        final Response response = (Response) mapper.map(actionValueResponseObject, clazz);

        if (response == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object for Action of class: "
                            + actionValueResponseObject.getClass().getName()));
        }

        return response;
    }

    private Class<?> getClazz(final ActionResponse actionValueResponseObject) throws FunctionalException {
        final Class<?> clazz = CLASS_MAP.get(actionValueResponseObject.getClass());

        if (clazz == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No Value Object class for Action class: "
                            + actionValueResponseObject.getClass().getName()));
        }

        return clazz;
    }

    private ConfigurableMapper getMapper(final ActionResponse actionValueResponseObject) throws FunctionalException {
        final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(actionValueResponseObject.getClass());

        if (mapper == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING, new RuntimeException("No mapper for Action class: "
                            + actionValueResponseObject.getClass().getName()));
        }

        return mapper;
    }

}
