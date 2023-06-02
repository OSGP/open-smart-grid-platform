//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.publiclighting.mocks.oslpdevice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DevicesContext {

  private final ConcurrentMap<String, DeviceState> mockedDevicesMap = new ConcurrentHashMap<>();

  public DeviceState getDeviceState(final String deviceUid) {
    return this.mockedDevicesMap.computeIfAbsent(deviceUid, DeviceState::new);
  }

  public void clear() {
    this.mockedDevicesMap.clear();
  }
}
