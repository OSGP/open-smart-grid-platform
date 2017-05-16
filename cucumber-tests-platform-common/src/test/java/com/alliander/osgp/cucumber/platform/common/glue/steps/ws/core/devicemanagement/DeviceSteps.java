/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.cucumber.core.GlueBase;
import com.alliander.osgp.cucumber.platform.PlatformKeys;

public class DeviceSteps extends GlueBase {

    public static void checkDevice(final Map<String, String> expectedDevice, final Device actualDevice) {
        if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_DEVICE_IDENTIFICATION),
                    actualDevice.getDeviceIdentification());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_ALIAS)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_ALIAS), actualDevice.getAlias());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_CITY)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_CITY), actualDevice.getContainerCity());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_MUNICIPALITY)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_MUNICIPALITY),
                    actualDevice.getContainerMunicipality());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_NUMBER)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_NUMBER), actualDevice.getContainerNumber());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_POSTCODE)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_POSTCODE), actualDevice.getContainerPostalCode());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_STREET)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_STREET), actualDevice.getContainerStreet());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_TYPE)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_DEVICE_TYPE), actualDevice.getDeviceType());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_UID)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_DEVICE_UID), actualDevice.getDeviceUid());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_LATITUDE)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_LATITUDE), actualDevice.getGpsLatitude());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_LONGITUDE)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_LONGITUDE), actualDevice.getGpsLongitude());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_NETWORKADDRESS)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_NETWORKADDRESS), actualDevice.getNetworkAddress());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_OWNER)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_OWNER), actualDevice.getOwner());
        }
    }
}
