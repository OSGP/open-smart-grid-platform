/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.core.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractOsgpSchedulerConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OsgpSchedulerConfig extends AbstractOsgpSchedulerConfig {

  public OsgpSchedulerConfig() {
    super("WsCoreQuartzScheduler");
  }
}
