/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.cucumber.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.cucumber.platform.GlueBase;
import com.alliander.osgp.cucumber.platform.Keys;

public class DeviceSteps extends GlueBase {

	public static void checkDevice(final Map<String, String> expectedDevice, final Device actualDevice) {
    	if (expectedDevice.containsKey(Keys.KEY_DEVICE_IDENTIFICATION)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_DEVICE_IDENTIFICATION), actualDevice.getDeviceIdentification());
    	}

    	if (expectedDevice.containsKey(Keys.KEY_ALIAS)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_ALIAS), actualDevice.getAlias());
    	}

    	if (expectedDevice.containsKey(Keys.KEY_CITY)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_CITY), actualDevice.getContainerCity());
    	}

    	if (expectedDevice.containsKey(Keys.KEY_MUNICIPALITY)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_MUNICIPALITY), actualDevice.getContainerMunicipality());
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

    	if (expectedDevice.containsKey(Keys.KEY_DEVICE_TYPE)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_DEVICE_TYPE), actualDevice.getDeviceType());
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

    	if (expectedDevice.containsKey(Keys.KEY_NETWORKADDRESS)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_NETWORKADDRESS), actualDevice.getNetworkAddress());
    	}

    	if (expectedDevice.containsKey(Keys.KEY_OWNER)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.KEY_OWNER), actualDevice.getOwner());
    	}
	}
}
