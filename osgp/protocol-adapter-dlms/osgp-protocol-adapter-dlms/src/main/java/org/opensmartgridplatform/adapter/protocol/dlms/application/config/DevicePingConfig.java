/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import java.time.Duration;
import org.opensmartgridplatform.shared.infra.networking.ping.Pinger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
