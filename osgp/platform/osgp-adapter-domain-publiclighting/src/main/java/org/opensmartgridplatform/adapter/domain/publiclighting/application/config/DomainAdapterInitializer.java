// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.publiclighting.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class DomainAdapterInitializer extends AbstractApplicationInitializer {

  public DomainAdapterInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/AdapterDomainPublicLighting/log-config");
  }
}
