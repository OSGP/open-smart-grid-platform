// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.domain.services;

import java.util.EnumMap;
import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.springframework.stereotype.Component;

/*
 * Component for resolving device response service for specific device types.
 * Per device type different handling of measurement report responses may be needed.
 * Therefore each device type will have its own device response service.
 */
@Component
public class DeviceResponseServiceRegistry {

  private final Map<DeviceType, DeviceResponseService> map = new EnumMap<>(DeviceType.class);

  public DeviceResponseService forDeviceType(final DeviceType key) {
    return this.map.get(key);
  }

  public void register(final DeviceType key, final DeviceResponseService value) {
    this.map.put(key, value);
  }
}
