// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.config;

import org.opensmartgridplatform.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Iec61850MockServerConfig {

  @Value("${iec61850.mock.networkaddress}")
  private String iec61850MockNetworkAddress;

  @Bean
  public String iec61850MockNetworkAddress() {
    return this.iec61850MockNetworkAddress;
  }

  @Bean(destroyMethod = "stop", initMethod = "start")
  public Iec61850MockServer iec61850MockServerPampus() {
    return new Iec61850MockServer("PAMPUS", "Pampus.icd", 62102, "WAGO61850Server");
  }

  @Bean(destroyMethod = "stop", initMethod = "start")
  public Iec61850MockServer iec61850MockServerMarkerWadden() {
    return new Iec61850MockServer("MARKER WADDEN", "MarkerWadden.icd", 62103, "WAGO61850Server");
  }

  @Bean(destroyMethod = "stop", initMethod = "start")
  public Iec61850MockServer iec61850MockServerSchoteroog() {
    return new Iec61850MockServer("SCHOTEROOG", "Schoteroog.icd", 62104, "WAGO61850Server");
  }

  @Bean(destroyMethod = "stop", initMethod = "start")
  public Iec61850MockServer iec61850MockServerWago() {
    return new Iec61850MockServer("WAGO", "WAGO123.icd", 62105, "WAGO123");
  }
}
