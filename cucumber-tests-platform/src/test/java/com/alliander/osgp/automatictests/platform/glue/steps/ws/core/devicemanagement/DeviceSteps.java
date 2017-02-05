/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.automatictests.platform.glue.steps.ws.core.devicemanagement;

import static com.alliander.osgp.automatictests.platform.core.Helpers.getString;

import java.util.Map;

import org.junit.Assert;

import com.alliander.osgp.adapter.ws.schema.core.devicemanagement.Device;
import com.alliander.osgp.automatictests.platform.Keys;
import com.alliander.osgp.automatictests.platform.StepsBase;

public class DeviceSteps extends StepsBase {

	public static void checkDevice(final Map<String, String> expectedDevice, final Device actualDevice) {
    	if (expectedDevice.containsKey(Keys.DEVICE_IDENTIFICATION)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.DEVICE_IDENTIFICATION), actualDevice.getDeviceIdentification());
    	}

    	if (expectedDevice.containsKey(Keys.ALIAS)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.ALIAS), actualDevice.getAlias());
    	}

    	if (expectedDevice.containsKey(Keys.CITY)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.CITY), actualDevice.getContainerCity());
    	}

    	if (expectedDevice.containsKey(Keys.MUNICIPALITY)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.MUNICIPALITY), actualDevice.getContainerMunicipality());
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

    	if (expectedDevice.containsKey(Keys.DEVICE_TYPE)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.DEVICE_TYPE), actualDevice.getDeviceType());
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

    	if (expectedDevice.containsKey(Keys.NETWORKADDRESS)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.NETWORKADDRESS), actualDevice.getNetworkAddress());
    	}

    	if (expectedDevice.containsKey(Keys.OWNER)) {
        	Assert.assertEquals(getString(expectedDevice, Keys.OWNER), actualDevice.getOwner());
    	}
	}
}
