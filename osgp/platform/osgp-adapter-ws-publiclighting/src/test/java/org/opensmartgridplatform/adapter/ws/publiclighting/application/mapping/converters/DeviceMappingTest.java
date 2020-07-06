/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.converters;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.publiclighting.application.mapping.AdHocManagementMapper;
import org.opensmartgridplatform.domain.core.entities.Device;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

class DeviceMappingTest {

    private static final String DEVICE_IDENTIFICATION = "TestDevice-1";
    private static final String DEVICE_TYPE = "SSLD";
    private static final String CONTAINER_CITY = "some-city";
    private static final Integer CONTAINER_NUMBER = 42;
    private static final String CONTAINER_POSTAL_CODE = "1234AB";
    private static final String CONTAINER_STREET = "some-street";
    private static final Float GPS_LATITUDE = 51.0f;
    private static final Float GPS_LONGITUDE = 5.0f;
    private static final AdHocManagementMapper mapper = new AdHocManagementMapper();

    @Test
    void testConvertToCore() {
        final Device device = mapper.map(this.adhocManagementDevice(), Device.class);

        assertThat(device).usingRecursiveComparison()
                .ignoringFields("creationTime", "modificationTime")
                .isEqualTo(this.coreDevice());
    }

    @Test
    void testConvertToAdHocMananagement() {
        final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device device = mapper.map(
                this.coreDevice(),
                org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device.class);

        assertThat(device).usingRecursiveComparison()
                .ignoringFields("deviceUid")
                .isEqualTo(this.adhocManagementDevice());
    }

    @Test
    void testConvertSsldToAdHocMananagement() {
        final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device device = mapper.map(
                this.ssld(), org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device.class);

        assertThat(device).usingRecursiveComparison()
                .ignoringFields("deviceUid")
                .isEqualTo(this.adhocManagementDevice());
    }

    private org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device adhocManagementDevice() {
        final org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device device = new org.opensmartgridplatform.adapter.ws.schema.publiclighting.adhocmanagement.Device();
        device.setDeviceIdentification(DEVICE_IDENTIFICATION);
        device.setContainerPostalCode(CONTAINER_POSTAL_CODE);
        device.setContainerCity(CONTAINER_CITY);
        device.setContainerStreet(CONTAINER_STREET);
        device.setContainerNumber(CONTAINER_NUMBER.toString());
        device.setGpsLatitude(GPS_LATITUDE);
        device.setGpsLongitude(GPS_LONGITUDE);
        device.setDeviceType(DEVICE_TYPE);
        device.setActivated(true);

        return device;
    }

    private Device coreDevice() {
        final Device device = new Device(DEVICE_IDENTIFICATION, null, this.address(), this.gpsCoordinates(), null);
        device.updateRegistrationData(null, DEVICE_TYPE);
        return device;
    }

    private GpsCoordinates gpsCoordinates() {
        return new GpsCoordinates(GPS_LATITUDE, GPS_LONGITUDE);
    }

    private Address address() {
        return new Address(CONTAINER_CITY, CONTAINER_POSTAL_CODE, CONTAINER_STREET, CONTAINER_NUMBER, null, null);
    }

    private Ssld ssld() {
        final Ssld ssld = new Ssld(DEVICE_IDENTIFICATION, null, this.address(), this.gpsCoordinates(), null);
        ssld.updateRegistrationData(null, DEVICE_TYPE);
        return ssld;
    }
}
