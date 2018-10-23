/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;

import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice;
import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.inputparsers.XmlGregorianCalendarInputParser;

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
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_POSTCODE),
                    actualDevice.getContainerPostalCode());
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
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_NETWORKADDRESS),
                    actualDevice.getNetworkAddress());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_OWNER)) {
            Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_OWNER), actualDevice.getOwner());
        }

        if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_TYPE)
                && getString(expectedDevice, PlatformKeys.KEY_DEVICE_TYPE).equals("LMD")) {
            final LightMeasurementDevice lmd = actualDevice.getLightMeasurementDevice();
            Assert.assertNotNull("Found device has no Light Measurement Device field", lmd);

            if (expectedDevice.containsKey(PlatformKeys.CODE)) {
                Assert.assertEquals(getString(expectedDevice, PlatformKeys.CODE), lmd.getCode());
            }

            if (expectedDevice.containsKey(PlatformKeys.KEY_LIGHTMEASUREMENT_COLOR)) {
                Assert.assertEquals(getString(expectedDevice, PlatformKeys.KEY_LIGHTMEASUREMENT_COLOR), lmd.getColor());
            }

            if (expectedDevice.containsKey(PlatformKeys.KEY_LIGHTMEASUREMENT_DIGITAL_INPUT)) {
                Assert.assertEquals(getShort(expectedDevice, PlatformKeys.KEY_LIGHTMEASUREMENT_DIGITAL_INPUT),
                        lmd.getDigitalInput());
            }

            if (expectedDevice.containsKey(PlatformKeys.KEY_LIGHTMEASUREMENT_LAST_COMMUNICATION_TIME)) {
                final XMLGregorianCalendar inputXMLGregorianCalendar = XmlGregorianCalendarInputParser
                        .parse(getString(expectedDevice, PlatformKeys.KEY_LIGHTMEASUREMENT_LAST_COMMUNICATION_TIME));
                Assert.assertEquals("Last communication time does not match",
                        inputXMLGregorianCalendar.toGregorianCalendar(),
                        lmd.getLastCommunicationTime().toGregorianCalendar());
            }
        }

    }
}
