/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceModel;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Manufacturer;
import org.opensmartgridplatform.domain.core.entities.DeviceAuthorization;
import org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceLifecycleStatus;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DeviceConverterHelper<T extends org.opensmartgridplatform.domain.core.entities.Device> {
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
        final Address containerAddress = new Address(source.getContainerCity(), source.getContainerPostalCode(),
                source.getContainerStreet(), source.getContainerNumber(), source.getContainerMunicipality());
        final GpsCoordinates gpsCoordinates = new GpsCoordinates(Float.valueOf(source.getGpsLatitude()),
                Float.valueOf(source.getGpsLongitude()));
        if (this.clazz.isAssignableFrom(SmartMeter.class)) {
            destination = (T) new SmartMeter(source.getDeviceIdentification(), source.getAlias(), containerAddress,
                    gpsCoordinates);
        } else {
            destination = (T) new Ssld(source.getDeviceIdentification(), source.getAlias(), containerAddress, gpsCoordinates,
                    null);
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

        final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device destination = new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device();
        destination.setAlias(source.getAlias());
        destination.setActivated(source.isActivated());
        destination.setDeviceLifecycleStatus(
                org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceLifecycleStatus
                        .valueOf(source.getDeviceLifecycleStatus().name()));
        if (!Objects.isNull(source.getContainerAddress())) {
            final Address containerAddress = source.getContainerAddress();
            destination.setContainerCity(containerAddress.getCity());
            destination.setContainerNumber(containerAddress.getNumber());
            destination.setContainerPostalCode(containerAddress.getPostalCode());
            destination.setContainerStreet(containerAddress.getStreet());
            destination.setContainerMunicipality(containerAddress.getMunicipality());
        }
        destination.setDeviceIdentification(source.getDeviceIdentification());
        destination.setDeviceType(source.getDeviceType());
        destination.setTechnicalInstallationDate(
                this.convertDateToXMLGregorianCalendar(source.getTechnicalInstallationDate()));

        if (!Objects.isNull(source.getGpsCoordinates())) {
            final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
            if (gpsCoordinates.getLatitude() != null) {
                destination.setGpsLatitude(Float.toString(gpsCoordinates.getLatitude()));
            }
            if (gpsCoordinates.getLongitude() != null) {
                destination.setGpsLongitude(Float.toString(gpsCoordinates.getLongitude()));
            }
        }

        destination
                .setNetworkAddress(source.getNetworkAddress() == null ? null : source.getNetworkAddress().toString());
        destination.setOwner(source.getOwner() == null ? "" : source.getOwner().getName());
        destination.getOrganisations().addAll(source.getOrganisations());

        destination.setInMaintenance(source.isInMaintenance());

        final List<org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceAuthorization> deviceAuthorizations = new ArrayList<>();
        for (final DeviceAuthorization deviceAuthorisation : source.getAuthorizations()) {
            final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceAuthorization newDeviceAuthorization = new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.DeviceAuthorization();

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
            final org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice destinationLmd = new org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice();
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
