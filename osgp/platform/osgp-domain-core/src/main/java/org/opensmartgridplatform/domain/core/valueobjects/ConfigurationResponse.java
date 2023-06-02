//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects;

import org.opensmartgridplatform.domain.core.entities.DeviceMessageStatus;

public class ConfigurationResponse {
  private final DeviceMessageStatus deviceMessageStatus;
  private final Configuration configuration;

  public ConfigurationResponse(
      final DeviceMessageStatus deviceMessageStatus, final Configuration configuration) {
    this.deviceMessageStatus = deviceMessageStatus;
    this.configuration = configuration;
  }

  public DeviceMessageStatus getDeviceMessageStatus() {
    return this.deviceMessageStatus;
  }

  public Configuration getConfiguration() {
    return this.configuration;
  }
}
