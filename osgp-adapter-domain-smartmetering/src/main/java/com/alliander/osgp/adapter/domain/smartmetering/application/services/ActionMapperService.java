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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.CommonMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ConfigurationMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.ManagementMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.MonitoringMapper;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.ActualMeterReadsRequestGasRequestDataConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.CustomValueToDtoConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.PeriodicReadsRequestGasDataConverter;
import com.alliander.osgp.adapter.domain.smartmetering.application.mapping.customconverters.SetEncryptionKeyExchangeOnGMeterDataConverter;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActionRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActivityCalendarData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsGasRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ActualMeterReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.AdministrativeStatusTypeData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.BundleMessageRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ClearAlarmRegisterData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.CoupleMbusDeviceRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.DefinableLoadProfileConfigurationData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.FindEventsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GenerateAndReplaceKeysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAdministrativeStatusData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAllAttributeValuesRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetAssociationLnObjectsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetConfigurationObjectRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetFirmwareVersionRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.MbusActionRequest;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsGasRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.PeriodicMeterReadsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ProfileGenericDataRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ReadAlarmRegisterData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.ScanMbusChannelsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetAlarmNotificationsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetClockConfigurationRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetConfigurationObjectRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetEncryptionKeyExchangeOnGMeterRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetKeysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetPushSetupAlarmRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SetPushSetupSmsRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecialDaysRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SpecificAttributeValueRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.SynchronizeTimeRequestData;
import com.alliander.osgp.domain.core.valueobjects.smartmetering.UpdateFirmwareRequestData;
import com.alliander.osgp.domain.smartmetering.exceptions.GatewayDeviceInvalidForMbusDeviceException;
import com.alliander.osgp.domain.smartmetering.exceptions.GatewayDeviceNotSetForMbusDeviceException;
import com.alliander.osgp.domain.smartmetering.exceptions.MbusChannelNotFoundException;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActivityCalendarDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ActualMeterReadsDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.AdministrativeStatusTypeDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.BundleMessagesRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ClearAlarmRegisterRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.CoupleMbusDeviceByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.DefinableLoadProfileConfigurationDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GenerateAndReplaceKeysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAllAttributeValuesRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAssociationLnObjectsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetFirmwareVersionRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetMbusEncryptionKeyStatusRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ProfileGenericDataRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ReadAlarmRegisterDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.ScanMbusChannelsRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetAlarmNotificationsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetClockConfigurationRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetConfigurationObjectRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetDeviceLifecycleStatusByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetKeysRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetMbusUserKeyByChannelRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupAlarmRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SetPushSetupSmsRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecialDaysRequestDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SpecificAttributeValueRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.SynchronizeTimeRequestDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.UpdateFirmwareRequestDto;
import com.alliander.osgp.shared.exceptionhandling.ComponentType;
import com.alliander.osgp.shared.exceptionhandling.FunctionalException;
import com.alliander.osgp.shared.exceptionhandling.FunctionalExceptionType;

import ma.glasnost.orika.impl.ConfigurableMapper;

@Service(value = "domainSmartMeteringActionMapperService")
@Validated
public class ActionMapperService {

    @Autowired
    @Qualifier("configurationMapper")
    private ConfigurationMapper configurationMapper;

    @Autowired
    private ManagementMapper managementMapper;

    @Autowired
    private MonitoringMapper monitoringMapper;

    @Autowired
    private CommonMapper commonMapper;

    @Autowired
    private PeriodicReadsRequestGasDataConverter periodicReadsRequestGasDataConverter;

    @Autowired
    private ActualMeterReadsRequestGasRequestDataConverter actualReadsRequestGasDataConverter;

    @Autowired
    private SetEncryptionKeyExchangeOnGMeterDataConverter setEncryptionKeyExchangeOnGMeterDataConverter;

    @Autowired
    private DomainHelperService domainHelperService;

    private static final Map<Class<? extends ActionRequest>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();
    private static final Map<Class<? extends ActionRequest>, CustomValueToDtoConverter<? extends ActionRequest, ? extends ActionRequestDto>> CUSTOM_CONVERTER_FOR_CLASS = new HashMap<>();
    private static final Map<Class<? extends ActionRequest>, Class<? extends ActionRequestDto>> CLASS_MAP = new HashMap<>();

    /**
     * Specifies to which DTO object the core object needs to be mapped.
     */
    static {
        CLASS_MAP.put(PeriodicMeterReadsRequestData.class, PeriodicMeterReadsRequestDataDto.class);
        CLASS_MAP.put(ActualMeterReadsRequestData.class, ActualMeterReadsDataDto.class);
        CLASS_MAP.put(SpecialDaysRequestData.class, SpecialDaysRequestDataDto.class);
        CLASS_MAP.put(ReadAlarmRegisterData.class, ReadAlarmRegisterDataDto.class);
        CLASS_MAP.put(FindEventsRequestData.class, FindEventsRequestDto.class);
        CLASS_MAP.put(GetAdministrativeStatusData.class, GetAdministrativeStatusDataDto.class);
        CLASS_MAP.put(AdministrativeStatusTypeData.class, AdministrativeStatusTypeDataDto.class);
        CLASS_MAP.put(ActivityCalendarData.class, ActivityCalendarDataDto.class);
        CLASS_MAP.put(SetAlarmNotificationsRequestData.class, SetAlarmNotificationsRequestDto.class);
        CLASS_MAP.put(SetConfigurationObjectRequestData.class, SetConfigurationObjectRequestDataDto.class);
        CLASS_MAP.put(SetPushSetupAlarmRequestData.class, SetPushSetupAlarmRequestDto.class);
        CLASS_MAP.put(SetPushSetupSmsRequestData.class, SetPushSetupSmsRequestDto.class);
        CLASS_MAP.put(SynchronizeTimeRequestData.class, SynchronizeTimeRequestDto.class);
        CLASS_MAP.put(GetAllAttributeValuesRequestData.class, GetAllAttributeValuesRequestDto.class);
        CLASS_MAP.put(GetFirmwareVersionRequestData.class, GetFirmwareVersionRequestDto.class);
        CLASS_MAP.put(UpdateFirmwareRequestData.class, UpdateFirmwareRequestDto.class);
        CLASS_MAP.put(SetKeysRequestData.class, SetKeysRequestDto.class);
        CLASS_MAP.put(SpecificAttributeValueRequestData.class, SpecificAttributeValueRequestDto.class);
        CLASS_MAP.put(GetAssociationLnObjectsRequestData.class, GetAssociationLnObjectsRequestDto.class);
        CLASS_MAP.put(CoupleMbusDeviceRequestData.class, GetAssociationLnObjectsRequestDto.class);
        CLASS_MAP.put(SetClockConfigurationRequestData.class, SetClockConfigurationRequestDto.class);
        CLASS_MAP.put(GetConfigurationObjectRequestData.class, GetConfigurationObjectRequestDataDto.class);
        CLASS_MAP.put(ProfileGenericDataRequestData.class, ProfileGenericDataRequestDataDto.class);
        CLASS_MAP.put(GenerateAndReplaceKeysRequestData.class, GenerateAndReplaceKeysRequestDataDto.class);
        CLASS_MAP.put(DefinableLoadProfileConfigurationData.class, DefinableLoadProfileConfigurationDto.class);
        CLASS_MAP.put(SetMbusUserKeyByChannelRequestData.class, SetMbusUserKeyByChannelRequestDataDto.class);
        CLASS_MAP.put(CoupleMbusDeviceByChannelRequestData.class, CoupleMbusDeviceByChannelRequestDataDto.class);
        CLASS_MAP.put(GetMbusEncryptionKeyStatusRequestData.class, GetMbusEncryptionKeyStatusRequestDto.class);
        CLASS_MAP.put(ClearAlarmRegisterData.class, ClearAlarmRegisterRequestDto.class);
        CLASS_MAP.put(GetMbusEncryptionKeyStatusByChannelRequestData.class,
                GetMbusEncryptionKeyStatusByChannelRequestDataDto.class);
        CLASS_MAP.put(SetDeviceLifecycleStatusByChannelRequestData.class,
                SetDeviceLifecycleStatusByChannelRequestDataDto.class);
        CLASS_MAP.put(ScanMbusChannelsRequestData.class, ScanMbusChannelsRequestDataDto.class);
    }

    /**
     * Specifies which mapper to use for the core class received.
     */
    @PostConstruct
    private void postConstruct() {

        CUSTOM_CONVERTER_FOR_CLASS.put(PeriodicMeterReadsGasRequestData.class,
                this.periodicReadsRequestGasDataConverter);
        CUSTOM_CONVERTER_FOR_CLASS.put(ActualMeterReadsGasRequestData.class, this.actualReadsRequestGasDataConverter);
        CUSTOM_CONVERTER_FOR_CLASS.put(SetEncryptionKeyExchangeOnGMeterRequestData.class,
                this.setEncryptionKeyExchangeOnGMeterDataConverter);

        CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsRequestData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(ActualMeterReadsRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(SpecialDaysRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(ReadAlarmRegisterData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(FindEventsRequestData.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(GetAdministrativeStatusData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(AdministrativeStatusTypeData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(ActivityCalendarData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetConfigurationObjectRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetAlarmNotificationsRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetConfigurationObjectRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetPushSetupAlarmRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetPushSetupSmsRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SynchronizeTimeRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(GetAllAttributeValuesRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(GetFirmwareVersionRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(UpdateFirmwareRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(SetKeysRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SpecificAttributeValueRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(GetAssociationLnObjectsRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(SetClockConfigurationRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(GetConfigurationObjectRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(ProfileGenericDataRequestData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(GenerateAndReplaceKeysRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(DefinableLoadProfileConfigurationData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetMbusUserKeyByChannelRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(CoupleMbusDeviceByChannelRequestData.class, this.commonMapper);
        CLASS_TO_MAPPER_MAP.put(GetMbusEncryptionKeyStatusRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(ClearAlarmRegisterData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(GetMbusEncryptionKeyStatusByChannelRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetDeviceLifecycleStatusByChannelRequestData.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(ScanMbusChannelsRequestData.class, this.configurationMapper);
    }

    public BundleMessagesRequestDto mapAllActions(final BundleMessageRequest bundleMessageDataContainer,
            final SmartMeter smartMeter) throws FunctionalException {

        final List<ActionDto> actionValueObjectDtoList = new ArrayList<>();

        for (ActionRequest action : bundleMessageDataContainer.getBundleList()) {

            @SuppressWarnings("unchecked")
            // suppress else the compiler will complain
            final CustomValueToDtoConverter<ActionRequest, ActionRequestDto> customValueToDtoConverter = (CustomValueToDtoConverter<ActionRequest, ActionRequestDto>) CUSTOM_CONVERTER_FOR_CLASS
                    .get(action.getClass());

            if (customValueToDtoConverter != null) {
                actionValueObjectDtoList.add(new ActionDto(customValueToDtoConverter.convert(action, smartMeter)));
            } else {
                final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
                final Class<? extends ActionRequestDto> clazz = CLASS_MAP.get(action.getClass());
                if (mapper != null) {

                    if (action instanceof MbusActionRequest) {
                        action = this.verifyAndFindChannelForMbusRequest((MbusActionRequest) action, smartMeter);
                    }

                    actionValueObjectDtoList.add(new ActionDto(this.performDefaultMapping(action, mapper, clazz)));
                } else {
                    throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                            ComponentType.DOMAIN_SMART_METERING,
                            new AssertionError("No mapper defined for class: " + clazz.getName()));
                }
            }
        }
        return new BundleMessagesRequestDto(actionValueObjectDtoList);
    }

    private ActionRequestDto performDefaultMapping(final ActionRequest action, final ConfigurableMapper mapper,
            final Class<? extends ActionRequestDto> clazz) throws FunctionalException {
        final ActionRequestDto actionValueObjectDto = mapper.map(action, clazz);

        if (actionValueObjectDto == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException("Object: " + action.getClass().getName()
                            + " could not be converted to " + clazz.getName()));
        }
        return actionValueObjectDto;
    }

    private ActionRequest verifyAndFindChannelForMbusRequest(final MbusActionRequest action,
            final SmartMeter smartMeter) throws FunctionalException {

        final SmartMeter mbusDevice = this.domainHelperService.findSmartMeter(action.getMbusDeviceIdentification());

        this.verifyMbusDeviceHasChannel(mbusDevice);
        this.verifyMbusDeviceHasGatewayDevice(mbusDevice);
        this.verifyMbusDeviceHasCorrectGatewayDevice(mbusDevice, smartMeter);

        action.setChannel(mbusDevice.getChannel());

        return action;
    }

    private void verifyMbusDeviceHasChannel(final SmartMeter mbusDevice) throws FunctionalException {
        if (mbusDevice.getChannel() == null) {
            throw new FunctionalException(FunctionalExceptionType.NO_MBUS_DEVICE_CHANNEL_FOUND,
                    ComponentType.DOMAIN_SMART_METERING,
                    new MbusChannelNotFoundException("M-Bus device should have a channel configured."));
        }
    }

    private void verifyMbusDeviceHasGatewayDevice(final SmartMeter mbusDevice) throws FunctionalException {
        if (mbusDevice.getGatewayDevice() == null) {
            throw new FunctionalException(FunctionalExceptionType.GATEWAY_DEVICE_NOT_SET_FOR_MBUS_DEVICE,
                    ComponentType.DOMAIN_SMART_METERING, new GatewayDeviceNotSetForMbusDeviceException());
        }
    }

    private void verifyMbusDeviceHasCorrectGatewayDevice(final SmartMeter mbusDevice, final SmartMeter smartMeter)
            throws FunctionalException {
        if (!smartMeter.getDeviceIdentification().equals(mbusDevice.getGatewayDevice().getDeviceIdentification())) {
            throw new FunctionalException(FunctionalExceptionType.GATEWAY_DEVICE_INVALID_FOR_MBUS_DEVICE,
                    ComponentType.DOMAIN_SMART_METERING, new GatewayDeviceInvalidForMbusDeviceException());
        }
    }

}
