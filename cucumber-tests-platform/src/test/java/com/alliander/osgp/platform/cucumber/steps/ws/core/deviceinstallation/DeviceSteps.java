package com.alliander.osgp.platform.cucumber.steps.ws.core.deviceinstallation;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.core.deviceinstallation.Device;
import com.alliander.osgp.platform.cucumber.steps.Keys;

public class DeviceSteps {

    private static Map<String, String> tempExpectedDevice;

    private static void checkAndAssert(final String key, final Object actualValue) {
        if (tempExpectedDevice.containsKey(key)) {

            Object expectedObj = null;

            if (actualValue instanceof String) {
                expectedObj = getString(tempExpectedDevice, key);
            } else if (actualValue instanceof Integer) {
                expectedObj = getInteger(tempExpectedDevice, key);
            } else if (actualValue instanceof Boolean) {
                expectedObj = getBoolean(tempExpectedDevice, key);
            }

            if (expectedObj != null) {
                Assert.assertEquals(expectedObj, actualValue);
            }
        }
    }

    public static void checkDevice(final Map<String, String> expectedDevice, final Device actualDevice) {
        tempExpectedDevice = expectedDevice;

        checkAndAssert(Keys.KEY_DEVICE_IDENTIFICATION, actualDevice.getDeviceIdentification());
        checkAndAssert(Keys.KEY_ALIAS, actualDevice.getAlias());
        checkAndAssert(Keys.KEY_CITY, actualDevice.getContainerCity());
        checkAndAssert(Keys.KEY_MUNICIPALITY, actualDevice.getContainerMunicipality());
        checkAndAssert(Keys.KEY_NUMBER, actualDevice.getContainerNumber());
        checkAndAssert(Keys.KEY_POSTCODE, actualDevice.getContainerPostalCode());
        checkAndAssert(Keys.KEY_STREET, actualDevice.getContainerStreet());
        checkAndAssert(Keys.KEY_DEVICE_UID, actualDevice.getDeviceUid());
        checkAndAssert(Keys.KEY_LATITUDE, actualDevice.getGpsLatitude());
        checkAndAssert(Keys.KEY_LONGITUDE, actualDevice.getGpsLongitude());
        checkAndAssert(Keys.KEY_OWNER, actualDevice.getOwner());
        checkAndAssert(Keys.KEY_HAS_SCHEDULE, actualDevice.isHasSchedule());

    }

    public static void checkDeviceOld(final Map<String, String> expectedDevice, final Device actualDevice) {
        if (expectedDevice.containsKey(Keys.KEY_DEVICE_IDENTIFICATION)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_DEVICE_IDENTIFICATION),
                    actualDevice.getDeviceIdentification());
        }

        if (expectedDevice.containsKey(Keys.KEY_ORGANIZATION_IDENTIFICATION)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_ORGANIZATION_IDENTIFICATION),
                    actualDevice.getDeviceIdentification());
        }

        if (expectedDevice.containsKey(Keys.KEY_ALIAS)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_ALIAS), actualDevice.getAlias());
        }

        if (expectedDevice.containsKey(Keys.KEY_CITY)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_CITY), actualDevice.getContainerCity());
        }

        if (expectedDevice.containsKey(Keys.KEY_MUNICIPALITY)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_MUNICIPALITY),
                    actualDevice.getContainerMunicipality());
        }

        if (expectedDevice.containsKey(Keys.KEY_NUMBER)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_NUMBER), actualDevice.getContainerNumber());
        }

        if (expectedDevice.containsKey(Keys.KEY_POSTCODE)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_POSTCODE), actualDevice.getContainerPostalCode());
        }

        if (expectedDevice.containsKey(Keys.KEY_STREET)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_STREET), actualDevice.getContainerStreet());
        }

        if (expectedDevice.containsKey(Keys.KEY_DEVICE_UID)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_DEVICE_UID), actualDevice.getDeviceUid());
        }

        if (expectedDevice.containsKey(Keys.KEY_LATITUDE)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_LATITUDE), actualDevice.getGpsLatitude());
        }

        if (expectedDevice.containsKey(Keys.KEY_LONGITUDE)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_LONGITUDE), actualDevice.getGpsLongitude());
        }

        if (expectedDevice.containsKey(Keys.KEY_OWNER)) {
            Assert.assertEquals(getString(expectedDevice, Keys.KEY_OWNER), actualDevice.getOwner());
        }
    }
}
