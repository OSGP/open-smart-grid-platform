/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceModel;
import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Manufacturer;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.LightMeasurementDevice;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.valueobjects.DeviceLifecycleStatus;

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
        T destination;
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

        if (source.isActivated() != null) {
            destination.setActivated(source.isActivated());
        }

        if (source.getDeviceLifecycleStatus() != null) {
            destination
                    .setDeviceLifecycleStatus(DeviceLifecycleStatus.valueOf(source.getDeviceLifecycleStatus().name()));
        }

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
        destination.setDeviceLifecycleStatus(
                com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus
                        .valueOf(source.getDeviceLifecycleStatus().name()));
        destination.setContainerCity(source.getContainerCity());
        destination.setContainerNumber(source.getContainerNumber());
        destination.setContainerPostalCode(source.getContainerPostalCode());
        destination.setContainerStreet(source.getContainerStreet());
        destination.setContainerMunicipality(source.getContainerMunicipality());
        destination.setDeviceIdentification(source.getDeviceIdentification());
        destination.setDeviceType(source.getDeviceType());
        destination.setTechnicalInstallationDate(
                this.convertDateToXMLGregorianCalendar(source.getTechnicalInstallationDate()));

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

        final List<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.DeviceAuthorization> deviceAuthorizations = new ArrayList<>();
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
            if (source.getDeviceModel().getManufacturer() != null) {
                final Manufacturer manufacturer = new Manufacturer();
                manufacturer.setManufacturerId(source.getDeviceModel().getManufacturer().getCode());
                manufacturer.setName(source.getDeviceModel().getManufacturer().getName());
                manufacturer.setUsePrefix(source.getDeviceModel().getManufacturer().isUsePrefix());
                deviceModel.setManufacturer(manufacturer);
            }
            deviceModel.setModelCode(source.getDeviceModel().getModelCode());
            deviceModel.setMetered(source.getDeviceModel().isMetered());
            destination.setDeviceModel(deviceModel);
        }

        if (source instanceof LightMeasurementDevice) {
            final LightMeasurementDevice sourceLmd = (LightMeasurementDevice) source;
            final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice destinationLmd = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice();
            destinationLmd.setDescription(sourceLmd.getDescription());
            destinationLmd.setCode(sourceLmd.getCode());
            destinationLmd.setColor(sourceLmd.getColor());
            destinationLmd.setDigitalInput(sourceLmd.getDigitalInput());
            destinationLmd.setLastCommunicationTime(
                    this.convertDateToXMLGregorianCalendar(sourceLmd.getLastCommunicationTime()));
            destination.setLightMeasurementDevice(destinationLmd);
        }

        return destination;
    }

    private XMLGregorianCalendar convertDateToXMLGregorianCalendar(final Date date) {
        XMLGregorianCalendar xmlCalendar = null;
        if (date != null) {
            final GregorianCalendar gCalendar = new GregorianCalendar();
            gCalendar.setTime(date);
            try {
                xmlCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gCalendar);
            } catch (final DatatypeConfigurationException dce) {
                LOGGER.error("Bad date format in 'date' parameter", dce);
            }
        }

        return xmlCalendar;
    }

}
