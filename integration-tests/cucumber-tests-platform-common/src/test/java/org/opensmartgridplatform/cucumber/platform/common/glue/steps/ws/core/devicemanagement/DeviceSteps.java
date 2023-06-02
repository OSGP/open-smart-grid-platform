//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getShort;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import javax.xml.datatype.XMLGregorianCalendar;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.Device;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.LightMeasurementDevice;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.inputparsers.XmlGregorianCalendarInputParser;

public class DeviceSteps {

  public static void checkDevice(
      final Map<String, String> expectedDevice, final Device actualDevice) {
    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
      assertThat(actualDevice.getDeviceIdentification())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    }

    if (expectedDevice.containsKey(PlatformKeys.ALIAS)) {
      assertThat(actualDevice.getAlias()).isEqualTo(getString(expectedDevice, PlatformKeys.ALIAS));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_CITY)) {
      assertThat(actualDevice.getContainerAddress().getCity())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_CITY));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_MUNICIPALITY)) {
      assertThat(actualDevice.getContainerAddress().getMunicipality())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_MUNICIPALITY));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_NUMBER)) {
      assertThat(actualDevice.getContainerAddress().getNumber())
          .isEqualTo(getInteger(expectedDevice, PlatformKeys.KEY_NUMBER));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_NUMBER_ADDITION)) {
      assertThat(actualDevice.getContainerAddress().getNumberAddition())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_NUMBER_ADDITION));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_POSTCODE)) {
      assertThat(actualDevice.getContainerAddress().getPostalCode())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_POSTCODE));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_STREET)) {
      assertThat(actualDevice.getContainerAddress().getStreet())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_STREET));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_TYPE)) {
      assertThat(actualDevice.getDeviceType())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_TYPE));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_UID)) {
      assertThat(actualDevice.getDeviceUid())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_UID));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_LATITUDE)) {
      assertThat(actualDevice.getGpsLatitude())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_LATITUDE));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_LONGITUDE)) {
      assertThat(actualDevice.getGpsLongitude())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_LONGITUDE));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_NETWORKADDRESS)) {
      assertThat(actualDevice.getNetworkAddress())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_NETWORKADDRESS));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_OWNER)) {
      assertThat(actualDevice.getOwner())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_OWNER));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_TYPE)
        && getString(expectedDevice, PlatformKeys.KEY_DEVICE_TYPE).equals("LMD")) {
      final LightMeasurementDevice lmd = actualDevice.getLightMeasurementDevice();
      assertThat(lmd).as("Found device has no Light Measurement Device field").isNotNull();

      if (expectedDevice.containsKey(PlatformKeys.CODE)) {
        assertThat(lmd.getCode()).isEqualTo(getString(expectedDevice, PlatformKeys.CODE));
      }

      if (expectedDevice.containsKey(PlatformKeys.KEY_LIGHTMEASUREMENT_COLOR)) {
        assertThat(lmd.getColor())
            .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_LIGHTMEASUREMENT_COLOR));
      }

      if (expectedDevice.containsKey(PlatformKeys.KEY_LIGHTMEASUREMENT_DIGITAL_INPUT)) {
        assertThat(lmd.getDigitalInput())
            .isEqualTo(getShort(expectedDevice, PlatformKeys.KEY_LIGHTMEASUREMENT_DIGITAL_INPUT));
      }

      if (expectedDevice.containsKey(PlatformKeys.KEY_LIGHTMEASUREMENT_LAST_COMMUNICATION_TIME)) {
        final XMLGregorianCalendar inputXMLGregorianCalendar =
            XmlGregorianCalendarInputParser.parse(
                getString(
                    expectedDevice, PlatformKeys.KEY_LIGHTMEASUREMENT_LAST_COMMUNICATION_TIME));
        assertThat(lmd.getLastCommunicationTime().toGregorianCalendar())
            .as("Last communication time does not match")
            .isEqualTo(inputXMLGregorianCalendar.toGregorianCalendar());
      }
    }
  }
}
