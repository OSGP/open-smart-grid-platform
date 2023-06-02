//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec60870.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class Iec60870ProtocolAdapterInitializer extends AbstractApplicationInitializer {

  public Iec60870ProtocolAdapterInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/AdapterProtocolIec60870/log-config");
  }
}
