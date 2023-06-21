// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.core.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class OsgpCoreInitializer extends AbstractApplicationInitializer {

  public OsgpCoreInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/Core/log-config");
  }
}
