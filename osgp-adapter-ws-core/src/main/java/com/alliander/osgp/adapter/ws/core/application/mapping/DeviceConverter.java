/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayStatus;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.RelayType;
import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;

class DeviceConverter extends
BidirectionalConverter<Device, com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConverter.class);

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

            if (source.getTechnicalInstallationDate() != null) {
                destination.setTechnicalInstallationDate(source.getTechnicalInstallationDate().toGregorianCalendar()
                        .getTime());
            }

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
                    newDeviceOutputSetting.setRelayType(deviceOutputSetting.getOutputType() == null ? null : RelayType
                            .valueOf(deviceOutputSetting.getOutputType().name()));
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

                addRelayStatusses(destination, ssld);
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

            setTechnicalInstallationDate(source, destination);

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

    private void setTechnicalInstallationDate(final Device source,
            final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination) {
        if (source.getTechnicalInstallationDate() != null) {
            final GregorianCalendar gCalendarTechnicalInstallation = new GregorianCalendar();
            gCalendarTechnicalInstallation.setTime(source.getTechnicalInstallationDate());
            try {
                destination.setTechnicalInstallationDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(
                        gCalendarTechnicalInstallation));
            } catch (final DatatypeConfigurationException e) {
                LOGGER.error("Bad date format in technical installation date", e);
            }
        }
    }

    private void addRelayStatusses(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination,
            final Ssld ssld) {
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