/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;

public class DeviceConverter
        extends BidirectionalConverter<org.opensmartgridplatform.domain.core.entities.Device, Device> {

    @Override
    public Device convertTo(final org.opensmartgridplatform.domain.core.entities.Device source,
            final Type<Device> destinationType, final MappingContext mappingContext) {
        final Device device = new Device();
        final String deviceIdentification = source.getDeviceIdentification();
        device.setDeviceUid(Base64.encodeBase64String(deviceIdentification.getBytes(StandardCharsets.US_ASCII)));
        device.setDeviceIdentification(deviceIdentification);
        final Address containerAddress = source.getContainerAddress();
        if (containerAddress != null) {
            device.setContainerPostalCode(containerAddress.getPostalCode());
            device.setContainerCity(containerAddress.getCity());
            device.setContainerStreet(containerAddress.getStreet());
            device.setContainerNumber(containerAddress.getNumber().toString());
        }
        final GpsCoordinates gpsCoordinates = source.getGpsCoordinates();
        if (gpsCoordinates != null) {
            device.setGpsLatitude(gpsCoordinates.getLatitude());
            device.setGpsLongitude(gpsCoordinates.getLongitude());
        }
        device.setDeviceType(source.getDeviceType());
        device.setActivated(source.isActivated());

        return device;
    }

    @Override
    public org.opensmartgridplatform.domain.core.entities.Device convertFrom(final Device source,
            final Type<org.opensmartgridplatform.domain.core.entities.Device> destinationType,
            final MappingContext mappingContext) {

        final String deviceIdentification = source.getDeviceIdentification();
        final Address containerAddress = new Address(source.getContainerCity(), source.getContainerPostalCode(),
                source.getContainerStreet(), Integer.valueOf(source.getContainerNumber()), "", "");
        final GpsCoordinates gpsCoordinates = new GpsCoordinates(source.getGpsLatitude(), source.getGpsLongitude());
        final org.opensmartgridplatform.domain.core.entities.Device device = new org.opensmartgridplatform.domain.core.entities.Device(
                deviceIdentification, "", containerAddress, gpsCoordinates, null);
        device.updateRegistrationData(null, source.getDeviceType());
        return device;
    }

    @Override
    public boolean canConvert(final Type<?> sourceType, final Type<?> destinationType) {
        return super.canConvert(sourceType, destinationType)
                || super.canConvert(sourceType.getSuperType(), destinationType);
    }

}
