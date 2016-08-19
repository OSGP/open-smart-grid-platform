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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceModel;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Manufacturer;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.entities.Ssld;

class DeviceConverterHelper<T extends com.alliander.osgp.domain.core.entities.Device> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceConverterHelper.class);

    private final Class<T> clazz;

    public DeviceConverterHelper(final Class<T> clazz) {
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    T initEntity(final Device source) {
        if (source.getGpsLatitude() == null) {
            source.setGpsLatitude("0");
        }
        if (source.getGpsLongitude() == null) {
            source.setGpsLongitude("0");
        }
        T destination = null;
        if (this.clazz.isAssignableFrom(SmartMeter.class)) {
            destination = (T) new SmartMeter(source.getDeviceIdentification(), source.getAlias(),
                    source.getContainerCity(), source.getContainerPostalCode(), source.getContainerStreet(),
                    source.getContainerNumber(), source.getContainerMunicipality(),
                    Float.valueOf(source.getGpsLatitude()), Float.valueOf(source.getGpsLongitude()));
        } else {
            destination = (T) new Ssld(source.getDeviceIdentification(), source.getAlias(), source.getContainerCity(),
                    source.getContainerPostalCode(), source.getContainerStreet(), source.getContainerNumber(),
                    source.getContainerMunicipality(), Float.valueOf(source.getGpsLatitude()),
                    Float.valueOf(source.getGpsLongitude()));
        }

        destination.setActivated(source.isActivated());
        destination.setActive(source.isActive());

        destination.updateRegistrationData(destination.getNetworkAddress(), source.getDeviceType());

        if (source.getTechnicalInstallationDate() != null) {
            destination.setTechnicalInstallationDate(
                    source.getTechnicalInstallationDate().toGregorianCalendar().getTime());
        }

        if (source.getDeviceModel() != null) {
            final DeviceModel deviceModel = new DeviceModel();
            deviceModel.setModelCode(source.getDeviceModel().getModelCode());
        }

        return destination;
    }

    Device initJaxb(final T source) {

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device();
        destination.setAlias(source.getAlias());
        destination.setActivated(source.isActivated());
        destination.setActive(source.isActive());
        destination.setContainerCity(source.getContainerCity());
        destination.setContainerNumber(source.getContainerNumber());
        destination.setContainerPostalCode(source.getContainerPostalCode());
        destination.setContainerStreet(source.getContainerStreet());
        destination.setContainerMunicipality(source.getContainerMunicipality());
        destination.setDeviceIdentification(source.getDeviceIdentification());
        destination.setDeviceType(source.getDeviceType());

        this.setTechnicalInstallationDate(source, destination);

        if (source.getGpsLatitude() != null) {
            destination.setGpsLatitude(Float.toString(source.getGpsLatitude()));
        }
        if (source.getGpsLongitude() != null) {
            destination.setGpsLongitude(Float.toString(source.getGpsLongitude()));
        }

        destination
                .setNetworkAddress(source.getNetworkAddress() == null ? null : source.getNetworkAddress().toString());
        destination.setOwner(source.getOwner() == null ? "" : source.getOwner().getName());
        destination.getOrganisations().addAll(source.getOrganisations());

        destination.setInMaintenance(source.isInMaintenance());

        final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization> deviceAuthorizations = new ArrayList<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization>();
        for (final DeviceAuthorization deviceAuthorisation : source.getAuthorizations()) {
            final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization newDeviceAuthorization = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization();

            newDeviceAuthorization.setFunctionGroup(deviceAuthorisation.getFunctionGroup().name());
            newDeviceAuthorization
                    .setOrganisation(deviceAuthorisation.getOrganisation().getOrganisationIdentification());
            deviceAuthorizations.add(newDeviceAuthorization);
        }
        destination.getDeviceAuthorizations().addAll(deviceAuthorizations);

        if (source.getDeviceModel() != null) {
            final DeviceModel deviceModel = new DeviceModel();
            deviceModel.setDescription(source.getDeviceModel().getDescription());
            if (source.getDeviceModel().getManufacturerId() != null) {
                final Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManufacturerId(source.getDeviceModel().getManufacturerId().getManufacturerId());
                manufacturer.setName(source.getDeviceModel().getManufacturerId().getName());
                manufacturer.setUsePrefix(source.getDeviceModel().getManufacturerId().isUsePrefix());
                deviceModel.setManufacturer(manufacturer);
            }
            deviceModel.setModelCode(source.getDeviceModel().getModelCode());
            destination.setDeviceModel(deviceModel);
        }

        return destination;
    }

    private void setTechnicalInstallationDate(final T source, final Device destination) {
        if (source.getTechnicalInstallationDate() != null) {
            final GregorianCalendar gCalendarTechnicalInstallation = new GregorianCalendar();
            gCalendarTechnicalInstallation.setTime(source.getTechnicalInstallationDate());
            try {
                destination.setTechnicalInstallationDate(
                        DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendarTechnicalInstallation));
            } catch (final DatatypeConfigurationException e) {
                LOGGER.error("Bad date format in technical installation date", e);
            }
        }
    }

}
