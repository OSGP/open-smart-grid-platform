/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.da.application.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.opensmartgridplatform.shared.application.config.AbstractWsAdapterInitializer;
import org.springframework.web.WebApplicationInitializer;

/** Web application Java configuration class. */
public class AdapterInitializer extends AbstractWsAdapterInitializer
    implements WebApplicationInitializer {

  public AdapterInitializer() {
    super(
        ApplicationContext.class, "java:comp/env/osgp/AdapterWsDistributionAutomation/log-config");
  }

  /** Handles startup of Spring context. */
  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    this.startUp(servletContext);
  }
}
