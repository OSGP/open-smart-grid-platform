// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.config;

import org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClient;
import org.opensmartgridplatform.adapter.protocol.dlms.simulator.trigger.SimulatorTriggerClientException;
import org.opensmartgridplatform.shared.application.config.AbstractConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DlmsSimulatorConfig extends AbstractConfig {

  @Value("${dynamic.properties.base.url}")
  private String dynamicPropertiesBaseUrl;

  @Value("${simulator.network.address:localhost}")
  private String simulatorNetworkAddress;

  @Bean
  public SimulatorTriggerClient simulatorTriggerClient() throws SimulatorTriggerClientException {
    return new SimulatorTriggerClient(this.dynamicPropertiesBaseUrl);
  }

  @Bean
  public String simulatorNetworkAddress() {
    return this.simulatorNetworkAddress;
  }
}
