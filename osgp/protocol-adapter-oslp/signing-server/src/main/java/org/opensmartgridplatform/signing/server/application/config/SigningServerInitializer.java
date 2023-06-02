//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.signing.server.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class SigningServerInitializer extends AbstractApplicationInitializer {

  public SigningServerInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/SigningServer/log-config");
  }
}
