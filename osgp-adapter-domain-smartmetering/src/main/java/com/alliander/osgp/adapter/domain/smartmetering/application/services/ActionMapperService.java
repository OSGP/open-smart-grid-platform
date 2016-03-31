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
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.SmartMeter;
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
import com.alliander.osgp.dto.valueobjects.smartmetering.ChannelDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.FindEventsQueryDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.GetAdministrativeStatusDataDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.KeySetDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodTypeDto;
import com.alliander.osgp.dto.valueobjects.smartmetering.PeriodicMeterReadsQueryDto;
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

    @Autowired
    private DomainHelperService domainHelperService;

    private static Map<Class<? extends ActionValueObject>, ConfigurableMapper> CLASS_TO_MAPPER_MAP = new HashMap<>();

    // @formatter:off
    /**
     * Specifies which mapper to use for the core class received.
     */
    @PostConstruct
    private void postConstruct() {
        // Omitted because it needs custom conversion
        // CLASS_TO_MAPPER_MAP.put(ActualMeterReadsGasData.class, this.monitoringMapper);
        // CLASS_TO_MAPPER_MAP.put(PeriodicMeterReadsQuery.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(SpecialDaysRequestData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(ReadAlarmRegisterData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(FindEventsQuery.class, this.managementMapper);
        CLASS_TO_MAPPER_MAP.put(ActualMeterReadsData.class, this.monitoringMapper);
        CLASS_TO_MAPPER_MAP.put(GetAdministrativeStatusData.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(PushSetupAlarm.class, this.configurationMapper);

        // ok to here
        CLASS_TO_MAPPER_MAP.put(SmsDetails.class, this.adhocMapper);
        CLASS_TO_MAPPER_MAP.put(AdministrativeStatusType.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SetConfigurationObjectRequest.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(PushSetupSms.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(ActivityCalendar.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(AlarmNotifications.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(KeySet.class, this.configurationMapper);
        CLASS_TO_MAPPER_MAP.put(SmartMeteringDevice.class, this.installationMapper);
    }

    /**
     * Specifies to which DTO object the core object needs to be mapped.
     */
    private static Map<Class<? extends ActionValueObject>, Class<? extends ActionValueObjectDto>> CLASS_MAP = new HashMap<>();
    static {
        // Omitted because it needs custom conversion
        // CLASS_MAP.put(ActualMeterReadsGasData.class, ActualMeterReadsDataGasDto.class);
        // CLASS_MAP.put(PeriodicMeterReadsQuery.class, PeriodicMeterReadsQueryDto.class);
        CLASS_MAP.put(SpecialDaysRequestData.class, SpecialDaysRequestDataDto.class);
        CLASS_MAP.put(ReadAlarmRegisterData.class, ReadAlarmRegisterRequestDto.class);
        CLASS_MAP.put(FindEventsQuery.class, FindEventsQueryDto.class);
        CLASS_MAP.put(ActualMeterReadsData.class, ActualMeterReadsDataDto.class);
        CLASS_MAP.put(GetAdministrativeStatusData.class, GetAdministrativeStatusDataDto.class);
        CLASS_MAP.put(PushSetupAlarm.class, PushSetupAlarmDto.class);

        // ok to here
        CLASS_MAP.put(SmsDetails.class, SmsDetailsDto.class);
        CLASS_MAP.put(AdministrativeStatusType.class, AdministrativeStatusTypeDto.class);
        CLASS_MAP.put(SetConfigurationObjectRequest.class, SetConfigurationObjectRequestDto.class);
        CLASS_MAP.put(PushSetupSms.class, PushSetupSmsDto.class);
        CLASS_MAP.put(ActivityCalendar.class, ActivityCalendarDto.class);
        CLASS_MAP.put(AlarmNotifications.class, AlarmNotificationsDto.class);
        CLASS_MAP.put(KeySet.class, KeySetDto.class);
        CLASS_MAP.put(SmartMeteringDevice.class, SmartMeteringDeviceDto.class);

    }

    // @formatter:on

    public BundleMessageDataContainerDto mapAllActions(final BundleMessageDataContainer bundleMessageDataContainer,
            final SmartMeter smartMeter) throws FunctionalException {

        final List<ActionValueObjectDto> actionValueObjectDtoList = new ArrayList<ActionValueObjectDto>();

        for (final ActionValueObject action : bundleMessageDataContainer.getBundleList()) {

            final ConfigurableMapper mapper = CLASS_TO_MAPPER_MAP.get(action.getClass());
            final Class<? extends ActionValueObjectDto> clazz = CLASS_MAP.get(action.getClass());
            if (mapper != null) {
                actionValueObjectDtoList.add(this.performDefaultMapping(action, mapper, clazz));
            } else {
                actionValueObjectDtoList.add(this.convertCoreToDto(action, clazz, smartMeter));
            }
        }

        return new BundleMessageDataContainerDto(actionValueObjectDtoList);
    }

    private ActionValueObjectDto convertCoreToDto(final ActionValueObject action,
            final Class<? extends ActionValueObjectDto> clazz, final SmartMeter smartMeter) throws FunctionalException {

        if (action instanceof PeriodicMeterReadsQuery) {
            return this.performPeriodicMeterReadsQueryMapping((PeriodicMeterReadsQuery) action, smartMeter);
        } else if (action instanceof ActualMeterReadsGasData) {
            return this.performActualMeterReadsGasDatayMapping((ActualMeterReadsGasData) action);
        } else {
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new AssertionError("No mapper defined for class: "
                            + clazz.getName()));
        }
    }

    private ActionValueObjectDto performActualMeterReadsGasDatayMapping(
            final ActualMeterReadsGasData actualMeterReadsGasData) throws FunctionalException {

        final SmartMeter gasMeter = this.domainHelperService.findSmartMeter(actualMeterReadsGasData
                .getDeviceIdentification());

        if (gasMeter.getChannel() == null) {
            /*
             * For now, throw a FunctionalException. As soon as we can
             * communicate with some types of gas meters directly, and not
             * through an M-Bus port of an energy meter, this will have to be
             * changed.
             */
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                            "Meter for gas reads should have a channel configured."));
        }
        final Device gatewayDevice = gasMeter.getGatewayDevice();
        if (gatewayDevice == null) {
            /*
             * For now throw a FunctionalException, based on the same reasoning
             * as with the channel a couple of lines up. As soon as we have
             * scenario's with direct communication with gas meters this will
             * have to be changed.
             */
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                            "Meter for gas reads should have an energy meter as gateway device."));
        }

        return new ActualMeterReadsDataGasDto(ChannelDto.fromNumber(gasMeter.getChannel()));
    }

    private ActionValueObjectDto performPeriodicMeterReadsQueryMapping(
            final PeriodicMeterReadsQuery periodicMeterReadsQuery, final SmartMeter smartMeter)
            throws FunctionalException {

        if (periodicMeterReadsQuery.isMbusDevice()) {

            final SmartMeter gasMeter = this.domainHelperService.findSmartMeter(periodicMeterReadsQuery
                    .getDeviceIdentification());

            if (gasMeter.getChannel() != null
                    && gasMeter.getGatewayDevice() != null
                    && gasMeter.getGatewayDevice().getDeviceIdentification() != null
                    && gasMeter.getGatewayDevice().getDeviceIdentification()
                            .equals(smartMeter.getDeviceIdentification())) {

                return new PeriodicMeterReadsQueryDto(PeriodTypeDto.valueOf(periodicMeterReadsQuery.getPeriodType()
                        .name()), periodicMeterReadsQuery.getBeginDate(), periodicMeterReadsQuery.getEndDate(),
                        ChannelDto.fromNumber(gasMeter.getChannel()));
            }
            /*
             * For now, throw a FunctionalException. As soon as we can
             * communicate with some types of gas meters directly, and not
             * through an M-Bus port of an energy meter, this will have to be
             * changed.
             */
            throw new FunctionalException(FunctionalExceptionType.VALIDATION_ERROR,
                    ComponentType.DOMAIN_SMART_METERING, new AssertionError(
                            "Meter for gas reads should have a channel configured."));

        } else {
            return this.monitoringMapper.map(periodicMeterReadsQuery, PeriodicMeterReadsQueryDto.class);
        }
    }

    private ActionValueObjectDto performDefaultMapping(final ActionValueObject action, final ConfigurableMapper mapper,
            final Class<? extends ActionValueObjectDto> clazz) throws FunctionalException {
        final ActionValueObjectDto actionValueObjectDto = mapper.map(action, clazz);

        if (actionValueObjectDto == null) {
            throw new FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
                    ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
                            "No Action Value Object DTO for Action Value Object of class: "
                                    + action.getClass().getName()));
        }
        return actionValueObjectDto;
    }

    // private Class<? extends ActionValueObjectDto> getClazz(final
    // ActionValueObject action) throws FunctionalException {
    // final Class<? extends ActionValueObjectDto> clazz = );
    //
    // if (clazz == null) {
    // throw new
    // FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
    // ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
    // "No Action Value Object DTO class for Action Value Object class: "
    // + action.getClass().getName()));
    // }
    // return clazz;
    // }

    // private ConfigurableMapper getMapper(final ActionValueObject action)
    // throws FunctionalException {
    // final ConfigurableMapper mapper =
    // classToMapperMap.get(action.getClass());
    //
    // if (mapper == null) {
    // throw new
    // FunctionalException(FunctionalExceptionType.UNSUPPORTED_DEVICE_ACTION,
    // ComponentType.DOMAIN_SMART_METERING, new RuntimeException(
    // "No mapper for Action Value Object class: " +
    // action.getClass().getName()));
    // }
    // return mapper;
    // }
}
