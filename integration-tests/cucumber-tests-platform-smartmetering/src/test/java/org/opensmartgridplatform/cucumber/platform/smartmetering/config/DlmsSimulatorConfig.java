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

  @Value("${web.service.truststore.location}")
  private String truststoreLocation;

  @Value("${web.service.truststore.password}")
  private String truststorePassword;

  @Value("${web.service.truststore.type}")
  private String truststoreType;

  @Value("${triggered.simulator.url}")
  private String baseAddress;

  @Bean
  public SimulatorTriggerClient simulatorTriggerClient() throws SimulatorTriggerClientException {

    return new SimulatorTriggerClient(
        this.truststoreLocation, this.truststorePassword, this.truststoreType, this.baseAddress);
  }
}
