// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.protocol.iec60870.config;

import org.opensmartgridplatform.cucumber.protocol.iec60870.mock.Iec60870MockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Iec60870MockServerConfig {

  @Value("${iec60870.mock.networkaddress}")
  private String iec60870MockNetworkAddress;

  @Value("${iec60870.mock.connection.timeout:10_000}")
  private int connectionTimeout;

  @Value("${iec60870.mock.port}")
  private int port;

  @Bean
  public String iec60870MockNetworkAddress() {
    return this.iec60870MockNetworkAddress;
  }

  @Bean(destroyMethod = "stop", initMethod = "start")
  public Iec60870MockServer iec60870MockServer() {
    final Iec60870MockServer mockServer = new Iec60870MockServer(this.port, this.connectionTimeout);

    return mockServer;
  }
}
