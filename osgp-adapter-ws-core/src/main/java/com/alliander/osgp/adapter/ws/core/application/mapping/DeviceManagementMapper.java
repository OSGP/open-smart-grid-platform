/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alliander.osgp.adapter.ws.core.application.mapping.ws.EventTypeConverter;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayStatus;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Ean;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.shared.mappers.XMLGregorianCalendarToDateTimeConverter;

@Component(value = "coreDeviceManagementMapper")
public class DeviceManagementMapper extends ConfigurableMapper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceManagementMapper.class);

    @Autowired
    private SsldRepository ssldRepository;

    public DeviceManagementMapper() {
        super(false);
    }

    @PostConstruct
    public void initialize() {
        this.init();
    }

    @Override
    public void configure(final MapperFactory mapperFactory) {
        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.Device.class,
                        com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device.class)
                .field("ipAddress", "networkAddress").byDefault().toClassMap());

        mapperFactory.registerClassMap(mapperFactory
                .classMap(com.alliander.osgp.domain.core.entities.Event.class,
                        com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Event.class)
                .field("device.deviceIdentification", "deviceIdentification").field("dateTime", "timestamp")
                .byDefault().toClassMap());

        mapperFactory.getConverterFactory().registerConverter(new XMLGregorianCalendarToDateTimeConverter());
        mapperFactory.getConverterFactory().registerConverter(new EventTypeConverter());
        mapperFactory.getConverterFactory().registerConverter(new DeviceConverter(this.ssldRepository));
        mapperFactory.getConverterFactory().registerConverter(new SsldConverter());
    }

    private static class SsldConverter extends
    BidirectionalConverter<Ssld, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> {

        /*
         * (non-Javadoc)
         *
         * @see
         * ma.glasnost.orika.converter.BidirectionalConverter#convertTo(java
         * .lang.Object, ma.glasnost.orika.metadata.Type)
         */
        @Override
        public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final Ssld source,
                final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {
            return DeviceConverter.convertToStatic(source, destinationType);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * ma.glasnost.orika.converter.BidirectionalConverter#convertFrom(java
         * .lang.Object, ma.glasnost.orika.metadata.Type)
         */
        @Override
        public Ssld convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
                final Type<Ssld> destinationType) {
            Ssld destination = null;

            if (source != null) {

                if (source.getGpsLatitude() == null) {
                    source.setGpsLatitude("0");
                }
                if (source.getGpsLongitude() == null) {
                    source.setGpsLongitude("0");
                }

                destination = new Ssld(source.getDeviceIdentification(), source.getAlias(), source.getContainerCity(),
                        source.getContainerPostalCode(), source.getContainerStreet(), source.getContainerNumber(),
                        source.getContainerMunicipality(), Float.valueOf(source.getGpsLatitude()), Float.valueOf(source
                                .getGpsLongitude()));

                final List<com.alliander.osgp.domain.core.entities.DeviceOutputSetting> deviceOutputSettings = new ArrayList<com.alliander.osgp.domain.core.entities.DeviceOutputSetting>();

                for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting deviceOutputSetting : source
                        .getOutputSettings()) {
                    com.alliander.osgp.domain.core.entities.DeviceOutputSetting newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting();

                    newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting(
                            deviceOutputSetting.getInternalId(), deviceOutputSetting.getExternalId(),
                            deviceOutputSetting.getRelayType() == null ? null
                                    : com.alliander.osgp.domain.core.valueobjects.RelayType.valueOf(deviceOutputSetting
                                            .getRelayType().name()), deviceOutputSetting.getAlias());

                    deviceOutputSettings.add(newDeviceOutputSetting);
                }
                destination.updateOutputSettings(deviceOutputSettings);
                destination.setPublicKeyPresent(source.isPublicKeyPresent());
                destination.setHasSchedule(source.isHasSchedule());
                destination.setActivated(source.isActivated());

                // clearing the existing Eans to prevent duplication
                destination.setEans(new ArrayList<Ean>());

                for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean ean : source.getEans()) {
                    final Ean newEan = new Ean(destination, ean.getCode(), ean.getDescription());

                    destination.getEans().add(newEan);
                }

                return destination;
            }
            return null;
        }

    }

    private static class DeviceConverter extends
            BidirectionalConverter<Device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> {

        private SsldRepository ssldRepository;

        private static DeviceConverter instance;

        public DeviceConverter(final SsldRepository ssldRepository) {
            this.ssldRepository = ssldRepository;
            instance = this;
        }

        public static Device convertFromStatic(
                final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
                final Type<Device> destinationType) {
            return instance.convertFrom(source, destinationType);
        }

        public static com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertToStatic(
                final Device source,
                final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {
            return instance.convertTo(source, destinationType);
        }

        @Override
        public Device convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
                final Type<Device> destinationType) {
            Ssld destination = null;

            if (source != null) {

                if (source.getGpsLatitude() == null) {
                    source.setGpsLatitude("0");
                }
                if (source.getGpsLongitude() == null) {
                    source.setGpsLongitude("0");
                }

                destination = new Ssld(source.getDeviceIdentification(), source.getAlias(), source.getContainerCity(),
                        source.getContainerPostalCode(), source.getContainerStreet(), source.getContainerNumber(),
                        source.getContainerMunicipality(), Float.valueOf(source.getGpsLatitude()), Float.valueOf(source
                                .getGpsLongitude()));

                final List<com.alliander.osgp.domain.core.entities.DeviceOutputSetting> deviceOutputSettings = new ArrayList<com.alliander.osgp.domain.core.entities.DeviceOutputSetting>();

                for (final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting deviceOutputSetting : source
                        .getOutputSettings()) {
                    com.alliander.osgp.domain.core.entities.DeviceOutputSetting newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting();

                    newDeviceOutputSetting = new com.alliander.osgp.domain.core.entities.DeviceOutputSetting(
                            deviceOutputSetting.getInternalId(), deviceOutputSetting.getExternalId(),
                            deviceOutputSetting.getRelayType() == null ? null
                                    : com.alliander.osgp.domain.core.valueobjects.RelayType.valueOf(deviceOutputSetting
                                            .getRelayType().name()), deviceOutputSetting.getAlias());

                    deviceOutputSettings.add(newDeviceOutputSetting);
                }
                destination.updateOutputSettings(deviceOutputSettings);
                destination.setPublicKeyPresent(source.isPublicKeyPresent());
                destination.setHasSchedule(source.isHasSchedule());

                return destination;
            }
            return null;
        }

        @Override
        public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final Device source,
                final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {

            final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device();

            if (source != null) {
                final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting> deviceOutputSettings = new ArrayList<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting>();

                final Ssld ssld = this.ssldRepository.findByDeviceIdentification(source.getDeviceIdentification());

                if (ssld != null) {
                    for (final DeviceOutputSetting deviceOutputSetting : ssld.getOutputSettings()) {
                        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting newDeviceOutputSetting = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceOutputSetting();

                        newDeviceOutputSetting.setExternalId(deviceOutputSetting.getExternalId());
                        newDeviceOutputSetting.setInternalId(deviceOutputSetting.getInternalId());
                        newDeviceOutputSetting.setRelayType(deviceOutputSetting.getOutputType() == null ? null
                                : RelayType.valueOf(deviceOutputSetting.getOutputType().name()));
                        newDeviceOutputSetting.setAlias(deviceOutputSetting.getAlias());
                        deviceOutputSettings.add(newDeviceOutputSetting);
                    }

                    destination.setPublicKeyPresent(ssld.isPublicKeyPresent());
                    destination.setHasSchedule(ssld.getHasSchedule());

                    final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean> eans = new ArrayList<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean>();
                    for (final com.alliander.osgp.domain.core.entities.Ean ean : ssld.getEans()) {
                        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean newEan = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Ean();
                        newEan.setCode(ean.getCode());
                        newEan.setDescription(ean.getDescription());
                        eans.add(newEan);
                    }
                    destination.getEans().addAll(eans);

                    if (ssld.getRelayStatusses() != null) {
                        RelayStatus temp = null;
                        for (final com.alliander.osgp.domain.core.entities.RelayStatus r : ssld.getRelayStatusses()) {
                            temp = this.convertRelayStatus(r);

                            if (temp != null) {
                                destination.getRelayStatuses().add(temp);
                            }
                        }
                    }
                }

                destination.getOutputSettings().addAll(deviceOutputSettings);

                destination.setAlias(source.getAlias());
                destination.setActivated(source.isActivated());
                destination.setContainerCity(source.getContainerCity());
                destination.setContainerNumber(source.getContainerNumber());
                destination.setContainerPostalCode(source.getContainerPostalCode());
                destination.setContainerStreet(source.getContainerStreet());
                destination.setContainerMunicipality(source.getContainerMunicipality());
                destination.setDeviceIdentification(source.getDeviceIdentification());
                destination.setDeviceType(source.getDeviceType());

                if (source.getGpsLatitude() != null) {
                    destination.setGpsLatitude(Float.toString(source.getGpsLatitude()));
                }
                if (source.getGpsLongitude() != null) {
                    destination.setGpsLongitude(Float.toString(source.getGpsLongitude()));
                }

                destination.setNetworkAddress(source.getNetworkAddress() == null ? null : source.getNetworkAddress()
                        .toString());
                destination.setOwner(source.getOwner() == null ? "" : source.getOwner().getName());
                destination.getOrganisations().addAll(source.getOrganisations());

                destination.setInMaintenance(source.isInMaintenance());

                final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization> deviceAuthorizations = new ArrayList<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization>();
                for (final DeviceAuthorization deviceAuthorisation : source.getAuthorizations()) {
                    final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization newDeviceAuthorization = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization();

                    newDeviceAuthorization.setFunctionGroup(deviceAuthorisation.getFunctionGroup().name());
                    newDeviceAuthorization.setOrganisation(deviceAuthorisation.getOrganisation()
                            .getOrganisationIdentification());
                    deviceAuthorizations.add(newDeviceAuthorization);
                }
                destination.getDeviceAuthorizations().addAll(deviceAuthorizations);

                return destination;
            }
            return null;
        }

        private RelayStatus convertRelayStatus(final com.alliander.osgp.domain.core.entities.RelayStatus status) {

            RelayStatus output = null;

            if (status != null) {

                output = new RelayStatus();
                output.setIndex(status.getIndex());
                output.setLastKnownState(status.isLastKnownState());

                final GregorianCalendar gCalendar = new GregorianCalendar();
                gCalendar.setTime(status.getLastKnowSwitchingTime());

                try {
                    output.setLastKnowSwitchingTime(DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar));
                } catch (final DatatypeConfigurationException e) {
                    // This won't happen, so no further action is needed.
                    LOGGER.error("Bad date format in one of theRelay Status dates", e);
                }
            }
            return output;
        }
    }
}
