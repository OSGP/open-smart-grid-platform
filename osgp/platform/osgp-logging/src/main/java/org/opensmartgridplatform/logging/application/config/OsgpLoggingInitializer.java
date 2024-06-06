// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.logging.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractWsAdapterInitializer;

/** Web application Java configuration class. */
public class OsgpLoggingInitializer extends AbstractWsAdapterInitializer {

  public OsgpLoggingInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/Logging/log-config");
  }
}
