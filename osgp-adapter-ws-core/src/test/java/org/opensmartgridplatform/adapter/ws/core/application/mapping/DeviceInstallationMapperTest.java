/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.domain.core.entities.Ssld;

public class DeviceInstallationMapperTest {

    private final static String DEVICE_IDENTIFICATION = "device_identification";
    private final static String ALIAS = "alias";
    private final static String CITY = "city";
    private final static String POSTAL_CODE = "postal_code";
    private final static String STREET = "street";
    private final static String NUMBER = "number";
    private final static String MUNICIPALITY = "municipality";
    private final static float GPS_LATITUDE = 50f;
    private final static float GPS_LONGITUDE = 5f;
    private final static boolean PUBLIC_KEY_PRESENT = true;

    DeviceInstallationMapper mapper = new DeviceInstallationMapper();

    @Before
    public void setup() {
        this.mapper.initialize();
    }

    @Test
    public void shouldConvertWsDeviceToSsld() {
        // given
        final Device device = this.createDevice();

        // when
        final Ssld ssld = this.mapper.map(device, Ssld.class);

        // then
        assertThat(ssld.getDeviceIdentification()).isEqualTo(DEVICE_IDENTIFICATION);
        assertThat(ssld.getAlias()).isEqualTo(ALIAS);
        assertThat(ssld.getContainerAddress()).isNotNull();
        assertThat(ssld.getContainerAddress().getCity()).isEqualTo(CITY);
        assertThat(ssld.getContainerAddress().getPostalCode()).isEqualTo(POSTAL_CODE);
        assertThat(ssld.getGpsCoordinates().getLatitude()).isEqualTo(GPS_LATITUDE);
        assertThat(ssld.getGpsCoordinates().getLongitude()).isEqualTo(GPS_LONGITUDE);
        assertThat(ssld.isPublicKeyPresent()).isEqualTo(PUBLIC_KEY_PRESENT);
    }

    private Device createDevice() {
        final Device device = new Device();
        device.setDeviceIdentification(DEVICE_IDENTIFICATION);
        device.setAlias(ALIAS);
        device.setContainerCity(CITY);
        device.setContainerPostalCode(POSTAL_CODE);
        device.setContainerStreet(STREET);
        device.setContainerNumber(NUMBER);
        device.setContainerMunicipality(MUNICIPALITY);
        device.setGpsLatitude(GPS_LATITUDE);
        device.setGpsLongitude(GPS_LONGITUDE);
        device.setPublicKeyPresent(PUBLIC_KEY_PRESENT);
        return device;
    }
}
