//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.time.Duration;
import org.opensmartgridplatform.shared.infra.networking.ping.Pinger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:osgp-adapter-protocol-dlms.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(value = "file:${osgp/AdapterProtocolDlms/config}", ignoreResourceNotFound = true)
public class DevicePingConfig {

  @Value("${device.ping.enabled:false}")
  private boolean pingingEnabled;

  @Value("${device.ping.count:1}")
  private int count;

  @Value("${device.ping.size:-1}")
  private int size;

  @Value("#{T(java.time.Duration).parse('${device.ping.timeout:PT0S}')}")
  private Duration timeout;

  @Value("${device.ping.lookup.host.names:false}")
  private Boolean lookupNamesForHostAddresses;

  public boolean pingingEnabled() {
    return this.pingingEnabled;
  }

  @Bean
  public Pinger pinger() {
    return new Pinger(this.count, this.size, this.timeout, this.lookupNamesForHostAddresses);
  }
}
