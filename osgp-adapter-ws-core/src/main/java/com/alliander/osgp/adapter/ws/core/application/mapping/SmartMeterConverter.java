/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.core.application.mapping;

import java.util.ArrayList;
import java.util.List;

import ma.glasnost.orika.metadata.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.SmartMeter;

class SmartMeterConverter extends AbstractDeviceConverter<SmartMeter> {
    static final Logger LOGGER = LoggerFactory.getLogger(SmartMeterConverter.class);

    @Override
    public SmartMeter convertFrom(final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device source,
            final Type<SmartMeter> destinationType) {
        return this.init(source, SmartMeter.class);
    }

    @Override
    public com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device convertTo(final SmartMeter source,
            final Type<com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device> destinationType) {

        final com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device destination = new com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device();

        if (source != null) {

            destination.setAlias(source.getAlias());
            destination.setActivated(source.isActivated());
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

}