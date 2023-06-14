// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
