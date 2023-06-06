// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import java.util.EnumMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DomainInfo;

public class DomainInfoFactory {

  private static final DomainInfo DOMAIN_INFO_DISTRIBUTION_AUTOMATION =
      new DomainInfo("DISTRIBUTION_AUTOMATION", "1.0");
  private static final DomainInfo DOMAIN_INFO_PUBLIC_LIGHTING =
      new DomainInfo("PUBLIC_LIGHTING", "1.0");
  private static Map<DeviceType, DomainInfo> DEVICE_TYPE_DOMAIN_INFO_MAP =
      new EnumMap<>(DeviceType.class);

  static {
    DEVICE_TYPE_DOMAIN_INFO_MAP.put(
        DeviceType.DISTRIBUTION_AUTOMATION_DEVICE, DOMAIN_INFO_DISTRIBUTION_AUTOMATION);
    DEVICE_TYPE_DOMAIN_INFO_MAP.put(DeviceType.LIGHT_SENSOR, DOMAIN_INFO_PUBLIC_LIGHTING);
    DEVICE_TYPE_DOMAIN_INFO_MAP.put(DeviceType.LIGHT_MEASUREMENT_RTU, DOMAIN_INFO_PUBLIC_LIGHTING);
  }

  public static DomainInfo forDeviceType(final DeviceType deviceType) {
    return DEVICE_TYPE_DOMAIN_INFO_MAP.getOrDefault(
        deviceType, DOMAIN_INFO_DISTRIBUTION_AUTOMATION);
  }
}
