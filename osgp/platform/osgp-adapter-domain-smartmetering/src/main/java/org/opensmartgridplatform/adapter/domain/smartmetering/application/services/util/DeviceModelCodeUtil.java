// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.domain.smartmetering.application.services.util;

import java.util.List;
import org.opensmartgridplatform.domain.core.entities.SmartMeter;

public class DeviceModelCodeUtil {
  private DeviceModelCodeUtil() {
    /// static class
  }

  public static String createDeviceModelCodes(
      final SmartMeter smartMeter, final List<SmartMeter> smartMeters) {

    final String emetercode =
        smartMeter.getDeviceModel() != null ? smartMeter.getDeviceModel().getModelCode() : "";
    final String mbus1code = getAndMapMbusChannelSmartMeter(smartMeters, 1);
    final String mbus2code = getAndMapMbusChannelSmartMeter(smartMeters, 2);
    final String mbus3code = getAndMapMbusChannelSmartMeter(smartMeters, 3);
    final String mbus4code = getAndMapMbusChannelSmartMeter(smartMeters, 4);

    return String.format("%s,%s,%s,%s,%s", emetercode, mbus1code, mbus2code, mbus3code, mbus4code);
  }

  private static String getAndMapMbusChannelSmartMeter(
      final List<SmartMeter> smartMeters, final int x) {
    return smartMeters.stream()
        .filter(m -> m.getChannel() == x)
        .map(m -> m.getDeviceModel() != null ? m.getDeviceModel().getModelCode() : "")
        .map(s -> s.replace(",", "")) // no comma's signs
        .findAny()
        .orElse("");
  }
}
