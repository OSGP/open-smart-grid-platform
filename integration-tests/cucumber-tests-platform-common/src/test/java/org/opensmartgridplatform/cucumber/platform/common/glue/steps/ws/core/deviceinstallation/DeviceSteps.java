/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.deviceinstallation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getFloat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.core.deviceinstallation.Device;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

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
        assertThat(actualValue).isEqualTo(expectedObj);
      }
    }
  }

  public static void checkDevice(
      final Map<String, String> expectedDevice, final Device actualDevice) {
    localExpectedDevice = expectedDevice;

    checkAndAssert(PlatformKeys.KEY_DEVICE_IDENTIFICATION, actualDevice.getDeviceIdentification());
    checkAndAssert(PlatformKeys.ALIAS, actualDevice.getAlias());
    checkAndAssert(PlatformKeys.KEY_CITY, actualDevice.getContainerAddress().getCity());
    checkAndAssert(
        PlatformKeys.KEY_MUNICIPALITY, actualDevice.getContainerAddress().getMunicipality());
    checkAndAssert(PlatformKeys.KEY_NUMBER, actualDevice.getContainerAddress().getNumber());
    checkAndAssert(PlatformKeys.KEY_POSTCODE, actualDevice.getContainerAddress().getPostalCode());
    checkAndAssert(PlatformKeys.KEY_STREET, actualDevice.getContainerAddress().getStreet());
    checkAndAssert(PlatformKeys.KEY_DEVICE_UID, actualDevice.getDeviceUid());
    checkAndAssert(PlatformKeys.KEY_LATITUDE, actualDevice.getGpsLatitude());
    checkAndAssert(PlatformKeys.KEY_LONGITUDE, actualDevice.getGpsLongitude());
    checkAndAssert(PlatformKeys.KEY_OWNER, actualDevice.getOwner());
    checkAndAssert(PlatformKeys.KEY_HAS_SCHEDULE, actualDevice.isHasSchedule());
  }

  public static void checkDeviceOld(
      final Map<String, String> expectedDevice, final Device actualDevice) {
    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_IDENTIFICATION)) {
      assertThat(actualDevice.getDeviceIdentification())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION)) {
      assertThat(actualDevice.getDeviceIdentification())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION));
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

    if (expectedDevice.containsKey(PlatformKeys.KEY_DEVICE_UID)) {
      assertThat(actualDevice.getDeviceUid())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_DEVICE_UID));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_LATITUDE)) {
      assertThat(actualDevice.getGpsLatitude())
          .isEqualTo(getFloat(expectedDevice, PlatformKeys.KEY_LATITUDE));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_LONGITUDE)) {
      assertThat(actualDevice.getGpsLongitude())
          .isEqualTo(getFloat(expectedDevice, PlatformKeys.KEY_LONGITUDE));
    }

    if (expectedDevice.containsKey(PlatformKeys.KEY_OWNER)) {
      assertThat(actualDevice.getOwner())
          .isEqualTo(getString(expectedDevice, PlatformKeys.KEY_OWNER));
    }
  }
}
