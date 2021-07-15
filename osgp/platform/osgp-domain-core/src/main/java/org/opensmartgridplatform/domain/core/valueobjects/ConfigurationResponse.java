/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
