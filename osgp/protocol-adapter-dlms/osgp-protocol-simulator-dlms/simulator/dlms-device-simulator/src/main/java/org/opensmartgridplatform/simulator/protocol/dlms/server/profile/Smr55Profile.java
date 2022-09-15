/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.server.profile;

import org.opensmartgridplatform.simulator.protocol.dlms.cosem.SingleActionScheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("smr55")
public class Smr55Profile {

  @Bean
  SingleActionScheduler lastGaspTestScheduler() {
    return new SingleActionScheduler("0.0.15.2.4.255");
  }
}
