/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.config;

import org.opensmartgridplatform.cucumber.core.config.BaseApplicationConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/** Base core device configuration. */
@Configuration
public class CoreDeviceConfiguration extends BaseApplicationConfiguration {

  @Value("${platform}")
  private String platform;

  @Value("${device.networkaddress}")
  private String deviceNetworkaddress;

  public String getPlatform() {
    return this.platform;
  }

  public String getDeviceNetworkAddress() {
    return this.deviceNetworkaddress;
  }
}
