// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
