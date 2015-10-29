/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.domain.core.entities;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import com.alliander.osgp.domain.core.exceptions.ArgumentNullOrEmptyException;
import com.alliander.osgp.domain.core.exceptions.PlatformException;
import com.alliander.osgp.domain.core.exceptions.ValidationException;
import com.alliander.osgp.oslp.Oslp.DeviceType;

public class DeviceTest {

    private static final String DEVICE_ID = "DEVICE-01";

    @Test
    public void createNewDevice() throws ValidationException, ArgumentNullOrEmptyException {
        final Device subject = new Device("DEV001");

        assertThat(subject.getDeviceIdentification(), equalTo("DEV001"));
    }

    //@Test
    public void updateRegistrationData() throws UnknownHostException, ValidationException, ArgumentNullOrEmptyException {
        final String deviceIdentification = "DEV002";
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final String deviceType = "PSLD";

        final Device subject = new Device(deviceIdentification);

        final Device device = new DeviceBuilder().withDeviceIdentification(deviceIdentification)
                .ofDeviceType(deviceType).isActivated(true).hasSchedule(true)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).build();

        subject.updateRegistrationData(address, deviceType);

        assertThat(subject, equalTo(device));
    }

    //@Test
    public void confirmRegistrationTest() throws UnknownHostException, PlatformException {
        // Set up the device and perform the registration step.
        final String deviceIdentification = "DEV002";
        final InetAddress address = InetAddress.getByName("127.0.0.1");
        final String deviceType = "PSLD";

        final Device subject = new Device(deviceIdentification);

        subject.updateRegistrationData(address, deviceType);
    }

    @Test
    public void deviceEqualsTest() throws UnknownHostException {
        final Device device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(false).hasSchedule(false)
                .ofDeviceType(DeviceType.SSLD.toString()).build();

        final Device expectedDevice = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(false).hasSchedule(false)
                .ofDeviceType(DeviceType.SSLD.toString()).build();

        assertTrue(device.equals(expectedDevice));
    }

    @Test
    public void deviceNotEqualsTest() {
        Device device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(false).hasSchedule(true)
                .ofDeviceType(DeviceType.SSLD.toString()).build();

        Device expectedDevice = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(true).hasSchedule(true)
                .ofDeviceType(DeviceType.SSLD.toString()).build();

        assertFalse(device.equals(expectedDevice));

        device = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(false).build();

        expectedDevice = new DeviceBuilder().withDeviceIdentification(DEVICE_ID)
                .withNetworkAddress(InetAddress.getLoopbackAddress()).isActivated(false)
                .ofDeviceType(DeviceType.SSLD.toString()).build();

        assertFalse(device.equals(expectedDevice));
    }
}
