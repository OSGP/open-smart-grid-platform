/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getBoolean;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getInteger;
import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device;
import com.alliander.osgp.automatictests.platform.Keys;

public class DeviceSteps {

    private static Map<String, String> localExpectedDevice;

    private static void checkAndAssert(final String key, final Object actualValue) {
        if (localExpectedDevice.containsKey(key)) {

            Object expectedObj = null;

            if (actualValue instanceof String) {
                expectedObj = getString(localExpectedDevice, key);
            } else if (actualValue instanceof Integer) {
                expectedObj = getInteger(localExpectedDevice, key);
            } else if (actualValue instanceof Boolean) {
                expectedObj = getBoolean(localExpectedDevice, key);
            }

            if (expectedObj != null) {
                Assert.assertEquals(expectedObj, actualValue);
            }
        }
    }

    public static void checkDevice(final Map<String, String> expectedDevice, final Device actualDevice) {
        localExpectedDevice = expectedDevice;

        checkAndAssert(Keys.DEVICE_IDENTIFICATION, actualDevice.getDeviceIdentification());
        checkAndAssert(Keys.ALIAS, actualDevice.getAlias());
        checkAndAssert(Keys.CITY, actualDevice.getContainerCity());
        checkAndAssert(Keys.MUNICIPALITY, actualDevice.getContainerMunicipality());
        checkAndAssert(Keys.NUMBER, actualDevice.getContainerNumber());
        checkAndAssert(Keys.POSTCODE, actualDevice.getContainerPostalCode());
        checkAndAssert(Keys.STREET, actualDevice.getContainerStreet());
        checkAndAssert(Keys.DEVICE_UID, actualDevice.getDeviceUid());
        checkAndAssert(Keys.LATITUDE, actualDevice.getGpsLatitude());
        checkAndAssert(Keys.LONGITUDE, actualDevice.getGpsLongitude());
        checkAndAssert(Keys.OWNER, actualDevice.getOwner());
        checkAndAssert(Keys.HAS_SCHEDULE, actualDevice.isHasSchedule());

    }

    public static void checkDeviceOld(final Map<String, String> expectedDevice, final Device actualDevice) {
        if (expectedDevice.containsKey(Keys.DEVICE_IDENTIFICATION)) {
            Assert.assertEquals(getString(expectedDevice, Keys.DEVICE_IDENTIFICATION),
                    actualDevice.getDeviceIdentification());
        }

        if (expectedDevice.containsKey(Keys.ORGANIZATION_IDENTIFICATION)) {
            Assert.assertEquals(getString(expectedDevice, Keys.ORGANIZATION_IDENTIFICATION),
                    actualDevice.getDeviceIdentification());
        }

        if (expectedDevice.containsKey(Keys.ALIAS)) {
            Assert.assertEquals(getString(expectedDevice, Keys.ALIAS), actualDevice.getAlias());
        }

        if (expectedDevice.containsKey(Keys.CITY)) {
            Assert.assertEquals(getString(expectedDevice, Keys.CITY), actualDevice.getContainerCity());
        }

        if (expectedDevice.containsKey(Keys.MUNICIPALITY)) {
            Assert.assertEquals(getString(expectedDevice, Keys.MUNICIPALITY),
                    actualDevice.getContainerMunicipality());
        }

        if (expectedDevice.containsKey(Keys.NUMBER)) {
            Assert.assertEquals(getString(expectedDevice, Keys.NUMBER), actualDevice.getContainerNumber());
        }

        if (expectedDevice.containsKey(Keys.POSTCODE)) {
            Assert.assertEquals(getString(expectedDevice, Keys.POSTCODE), actualDevice.getContainerPostalCode());
        }

        if (expectedDevice.containsKey(Keys.STREET)) {
            Assert.assertEquals(getString(expectedDevice, Keys.STREET), actualDevice.getContainerStreet());
        }

        if (expectedDevice.containsKey(Keys.DEVICE_UID)) {
            Assert.assertEquals(getString(expectedDevice, Keys.DEVICE_UID), actualDevice.getDeviceUid());
        }

        if (expectedDevice.containsKey(Keys.LATITUDE)) {
            Assert.assertEquals(getString(expectedDevice, Keys.LATITUDE), actualDevice.getGpsLatitude());
        }

        if (expectedDevice.containsKey(Keys.LONGITUDE)) {
            Assert.assertEquals(getString(expectedDevice, Keys.LONGITUDE), actualDevice.getGpsLongitude());
        }

        if (expectedDevice.containsKey(Keys.OWNER)) {
            Assert.assertEquals(getString(expectedDevice, Keys.OWNER), actualDevice.getOwner());
        }
    }
}
