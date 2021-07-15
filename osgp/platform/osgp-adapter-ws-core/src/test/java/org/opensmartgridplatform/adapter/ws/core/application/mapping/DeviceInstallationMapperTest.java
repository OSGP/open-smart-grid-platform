/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.LightMeasurementDevice;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.valueobjects.Address;
import org.opensmartgridplatform.domain.core.valueobjects.GpsCoordinates;

public class DeviceInstallationMapperTest {

    private final static String DEVICE_IDENTIFICATION = "device_identification";
    private final static String ALIAS = "alias";
    private final static String CITY = "city";
    private final static String POSTAL_CODE = "postal_code";
    private final static String STREET = "street";
    private final static int NUMBER = 83;
    private final static String NUMBER_ADDITION = "D";
    private final static String MUNICIPALITY = "municipality";
    private final static float GPS_LATITUDE = 50f;
    private final static float GPS_LONGITUDE = 5f;
    // SSLD test data
    private final static boolean PUBLIC_KEY_PRESENT = true;
    // LMD test data
    private final static String DESCRIPTION = "description";
    private final static String CODE = "code";
    private final static String COLOR = "color";
    private final static short DIGITAL_INPUT = 1;

    DeviceInstallationMapper mapper = new DeviceInstallationMapper();

    private Address createAddress() {
        return new Address(CITY, POSTAL_CODE, STREET, NUMBER, NUMBER_ADDITION, MUNICIPALITY);
    }

    private Ssld createSsld() {
        final Address containerAddress = this.createAddress();
        final GpsCoordinates gps = new GpsCoordinates(GPS_LATITUDE, GPS_LONGITUDE);

        final Ssld ssld = new Ssld(DEVICE_IDENTIFICATION, ALIAS, containerAddress, gps, null);
        ssld.setPublicKeyPresent(PUBLIC_KEY_PRESENT);
        return ssld;
    }

    private org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice createLmd() {
        final Address containerAddress = this.createAddress();
        final GpsCoordinates gps = new GpsCoordinates(GPS_LATITUDE, GPS_LONGITUDE);

        final org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice lmd = new org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice(
                DEVICE_IDENTIFICATION, ALIAS, containerAddress, gps, null);
        lmd.setDescription(DESCRIPTION);
        lmd.setCode(CODE);
        lmd.setColor(COLOR);
        lmd.setDigitalInput(DIGITAL_INPUT);
        return lmd;
    }

    private org.opensmartgridplatform.adapter.ws.schema.core.common.Address createWsAddress() {
        final org.opensmartgridplatform.adapter.ws.schema.core.common.Address address = new org.opensmartgridplatform.adapter.ws.schema.core.common.Address();
        address.setCity(CITY);
        address.setPostalCode(POSTAL_CODE);
        address.setStreet(STREET);
        address.setNumber(NUMBER);
        address.setNumberAddition(NUMBER_ADDITION);
        address.setMunicipality(MUNICIPALITY);
        return address;
    }

    private Device createWsDevice() {
        final Device device = new Device();
        device.setDeviceIdentification(DEVICE_IDENTIFICATION);
        device.setAlias(ALIAS);
        device.setContainerAddress(this.createWsAddress());
        device.setGpsLatitude(GPS_LATITUDE);
        device.setGpsLongitude(GPS_LONGITUDE);
        device.setPublicKeyPresent(PUBLIC_KEY_PRESENT);
        return device;
    }

    private LightMeasurementDevice createWsLmd() {
        final LightMeasurementDevice lmd = new LightMeasurementDevice();
        lmd.setDeviceIdentification(DEVICE_IDENTIFICATION);
        lmd.setAlias(ALIAS);
        lmd.setContainerAddress(this.createWsAddress());
        lmd.setGpsLatitude(GPS_LATITUDE);
        lmd.setGpsLongitude(GPS_LONGITUDE);
        lmd.setDescription(DESCRIPTION);
        lmd.setCode(CODE);
        lmd.setColor(COLOR);
        lmd.setDigitalInput(DIGITAL_INPUT);
        return lmd;
    }

    @BeforeEach
    public void setup() {
        this.mapper.initialize();
    }

    @Test
    public void testConversionFromWsDeviceToSsld() {
        // Arrange
        final Device device = this.createWsDevice();
        final Ssld expected = this.createSsld();

        // Act
        final Ssld actual = this.mapper.map(device, Ssld.class);

        // Assert
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "creationTime", "modificationTime");
    }

    @Test
    public void testConversionFromWsLmdToLmd() {
        // Arrange
        final LightMeasurementDevice lmd = this.createWsLmd();
        final org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice expected = this.createLmd();

        // Act
        final org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice actual = this.mapper.map(lmd,
                org.opensmartgridplatform.domain.core.entities.LightMeasurementDevice.class);

        // Assert
        assertThat(actual).isEqualToIgnoringGivenFields(expected, "creationTime", "modificationTime");
    }
}
