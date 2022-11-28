/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

public class ProtocolAdapterMqttInitializer extends AbstractApplicationInitializer {

  private static final String LOG_CONFIG = "java:comp/env/osgp/AdapterProtocolMqtt/log-config";

  public ProtocolAdapterMqttInitializer() {
    super(ApplicationContext.class, LOG_CONFIG);
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
  }
}
