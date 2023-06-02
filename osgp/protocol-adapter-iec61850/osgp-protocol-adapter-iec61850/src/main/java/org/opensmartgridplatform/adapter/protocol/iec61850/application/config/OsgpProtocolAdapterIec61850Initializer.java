//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.application.config;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class OsgpProtocolAdapterIec61850Initializer extends AbstractApplicationInitializer {

  public OsgpProtocolAdapterIec61850Initializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/AdapterProtocolIec61850/log-config");
    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);
  }
}
