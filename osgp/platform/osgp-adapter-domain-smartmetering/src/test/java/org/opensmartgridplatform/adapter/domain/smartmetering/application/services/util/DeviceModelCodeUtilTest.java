// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;

@ExtendWith(MockitoExtension.class)
class DeviceModelCodeUtilTest {

  @Test
  void shouldGiveAllDeviceModelCodes() {
    final SmartMeter baseMeter = createSmartMeterMock("base meter", null);
    final SmartMeter gmeter1 = createSmartMeterMock("g-meter 1", (short) 1);
    final SmartMeter gmeter2 = createSmartMeterMock("g-meter 2", (short) 2);
    final SmartMeter gmeter3 = createSmartMeterMock("g-meter 3", (short) 3);
    final SmartMeter gmeter4 = createSmartMeterMock("g-meter 4", (short) 4);

    final String codes =
        DeviceModelCodeUtil.createDeviceModelCodes(
            baseMeter, Arrays.asList(gmeter1, gmeter2, gmeter3, gmeter4));

    assertEquals("base meter,g-meter 1,g-meter 2,g-meter 3,g-meter 4", codes);
  }

  @Test
  void createDeviceModelCodesShouldGiveSomeDeviceModelCodes() {
    final SmartMeter baseMeter = createSmartMeterMock("base meter", null);
    final SmartMeter gmeter1 = createSmartMeterMock("g-meter 1", (short) 1);
    final SmartMeter gmeter3 = createSmartMeterMock("g-meter 3", (short) 3);

    final String codes =
        DeviceModelCodeUtil.createDeviceModelCodes(baseMeter, Arrays.asList(gmeter1, gmeter3));

    assertEquals("base meter,g-meter 1,,g-meter 3,", codes);
  }

  @Test
  void createDeviceModelCodesShouldBeSafeForNullChannel() {
    final SmartMeter baseMeter = createSmartMeterMock("base meter", null);
    final SmartMeter gmeter1 = createSmartMeterMock(null, null);

    final String codes =
        DeviceModelCodeUtil.createDeviceModelCodes(baseMeter, Collections.singletonList(gmeter1));
    assertEquals("base meter,,,,", codes);
  }

  private static SmartMeter createSmartMeterMock(final String code, final Short channel) {
    final SmartMeter smartMeter = Mockito.mock(SmartMeter.class);

    if (code != null) {
      final DeviceModel deviceModel = Mockito.mock(DeviceModel.class);
      when(deviceModel.getModelCode()).thenReturn(code);
      when(smartMeter.getDeviceModel()).thenReturn(deviceModel);
    }

    if (channel != null) {
      when(smartMeter.getChannel()).thenReturn(channel);
    }

    return smartMeter;
  }
}
