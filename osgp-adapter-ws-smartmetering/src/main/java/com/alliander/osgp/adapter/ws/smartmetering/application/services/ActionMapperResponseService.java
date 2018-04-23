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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsGasResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActualMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AllResponses;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.BundleResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FindEventsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetFirmwareVersionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusByChannelResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetMbusEncryptionKeyStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ObjectFactory;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsGasResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.PeriodicMeterReadsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ReadAlarmRegisterResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ScanMbusChannelsResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetDeviceLifecycleStatusByChannelResponse;
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
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessagesResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.EventMessagesResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FaultResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FirmwareVersionResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAllAttributeValuesResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationObjectResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReads;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MeterReadsGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainer;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsContainerGas;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataResponse;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ScanMbusChannelsResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelResponseData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.UpdateFirmwareResponse;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

import ma.glasnost.orika.impl.ConfigurableMapper;

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
    private MonitoringMapper monitoringMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private InstallationMapper installationMapper;

    private static Map<Class<? extends ActionResponse>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();
    private static Map<Class<? extends ActionResponse>, Class<?>> CLASS_MAP = new HashMap<>();

    /**
     * Specifies which mapper to use for the core object class received.
     */
    @PostConstruct
    private void postConstruct() {
        CLASS_TO_MAPPER_MAP.put(MeterReads.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(MeterReadsGas.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(EventMessagesResponse.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(ActionResponse.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(FaultResponse.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(AlarmRegister.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(AdministrativeStatusTypeResponse.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsContainer.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsContainerGas.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(GetAllAttributeValuesResponse.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(FirmwareVersionResponse.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(UpdateFirmwareResponse.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(AssociationLnObjectsResponseData.class, this.adhocMapper);
        CLASS_TO_MAPPER_MAP.put(GetConfigurationObjectResponse.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(ProfileGenericDataResponse.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(CoupleMbusDeviceByChannelResponse.class, this.installationMapper);
        CLASS_TO_MAPPER_MAP.put(GetMbusEncryptionKeyStatusResponseData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(GetMbusEncryptionKeyStatusByChannelResponseData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetDeviceLifecycleStatusByChannelResponseData.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(ScanMbusChannelsResponseData.class, this.adhocMapper);
    }

    /**
     * Specifies to which ws object the core object needs to be mapped.
     */
    static {
        CLASS_MAP.put(MeterReadsGas.class, ActualMeterReadsGasResponse.class);
        CLASS_MAP.put(MeterReads.class, ActualMeterReadsResponse.class);
        CLASS_MAP.put(EventMessagesResponse.class, FindEventsResponse.class);
        CLASS_MAP.put(ActionResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse.class);
        CLASS_MAP.put(FaultResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.FaultResponse.class);
        CLASS_MAP.put(AlarmRegister.class, ReadAlarmRegisterResponse.class);
        CLASS_MAP.put(AdministrativeStatusTypeResponse.class, AdministrativeStatusResponse.class);
        CLASS_MAP.put(PeriodicMeterReadsContainer.class, PeriodicMeterReadsResponse.class);
        CLASS_MAP.put(PeriodicMeterReadsContainerGas.class, PeriodicMeterReadsGasResponse.class);
        CLASS_MAP.put(GetAllAttributeValuesResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetAllAttributeValuesResponse.class);
        CLASS_MAP.put(FirmwareVersionResponse.class, GetFirmwareVersionResponse.class);
        CLASS_MAP.put(UpdateFirmwareResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.UpdateFirmwareResponse.class);
        CLASS_MAP.put(AssociationLnObjectsResponseData.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.AssociationLnObjectsResponse.class);
        CLASS_MAP.put(GetConfigurationObjectResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.GetConfigurationObjectResponse.class);
        CLASS_MAP.put(ProfileGenericDataResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ProfileGenericDataResponse.class);
        CLASS_MAP.put(CoupleMbusDeviceByChannelResponse.class,
                com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.CoupleMbusDeviceByChannelResponse.class);
        CLASS_MAP.put(GetMbusEncryptionKeyStatusResponseData.class, GetMbusEncryptionKeyStatusResponse.class);
        CLASS_MAP.put(GetMbusEncryptionKeyStatusByChannelResponseData.class,
                GetMbusEncryptionKeyStatusByChannelResponse.class);
        CLASS_MAP.put(SetDeviceLifecycleStatusByChannelResponseData.class,
                SetDeviceLifecycleStatusByChannelResponse.class);
        CLASS_MAP.put(ScanMbusChannelsResponseData.class, ScanMbusChannelsResponse.class);
    }

    public BundleResponse mapAllActions(final Serializable actionList) throws FunctionalException {
        final BundleMessagesResponse bundleResponseMessageDataContainer = (BundleMessagesResponse) actionList;
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
                    ComponentType.WS_SMART_METERING,
                    new RuntimeException("No Response Object of class " + (clazz == null ? "null" : clazz.getName())
                            + " for ActionResponse Value Object of class: "
                            + actionValueResponseObject.getClass().getName()));
        }

        return response;
    }

    private Class<?> getClazz(final ActionResponse actionValueResponseObject) throws FunctionalException {
        final Class<?> clazz = CLASS_MAP.get(actionValueResponseObject.getClass());

        if (clazz == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING,
                    new RuntimeException("No Response class for ActionResponse Value Object class: "
                            + actionValueResponseObject.getClass().getName()));
        }

        return clazz;
    }

    private ConfigurableMapper getMapper(final ActionResponse actionValueResponseObject) throws FunctionalException {
        final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(actionValueResponseObject.getClass());

        if (mapper == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.WS_SMART_METERING,
                    new RuntimeException("No mapper for ActionResponse Value Object class: "
                            + actionValueResponseObject.getClass().getName()));
        }

        return mapper;
    }

}
