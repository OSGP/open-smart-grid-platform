/* 
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.triggered.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;

/** Web application Java configuration class. */
public class OsgpSimulatorDlmsWebInitializer extends AbstractApplicationInitializer
    implements WebApplicationInitializer {

  private static final String SERVLET_NAME = "CXFServlet";
  private static final String SERVLET_MAPPING = "/*";

  public OsgpSimulatorDlmsWebInitializer() {
    super(ApplicationContext.class, "java:/comp/env/osgp/SimulatorDlmsTriggered/log-config");
  }

  /** Handles the startup of spring. */
  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    this.startUp(servletContext);
    this.addApacheCxfServlet(servletContext);
  }

  private void addApacheCxfServlet(final ServletContext servletContext) {
    final CXFServlet cxfServlet = new CXFServlet();
    final ServletRegistration.Dynamic appServlet =
        servletContext.addServlet(SERVLET_NAME, cxfServlet);
    appServlet.setLoadOnStartup(1);
    appServlet.addMapping(SERVLET_MAPPING);
  }
}
