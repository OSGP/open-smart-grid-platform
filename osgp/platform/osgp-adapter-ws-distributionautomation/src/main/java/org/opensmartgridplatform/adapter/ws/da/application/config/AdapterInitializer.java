//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.da.application.config;

import org.opensmartgridplatform.shared.application.config.AbstractWsAdapterInitializer;

/** Web application Java configuration class. */
public class AdapterInitializer extends AbstractWsAdapterInitializer {

  public AdapterInitializer() {
    super(
        ApplicationContext.class, "java:comp/env/osgp/AdapterWsDistributionAutomation/log-config");
  }
}
