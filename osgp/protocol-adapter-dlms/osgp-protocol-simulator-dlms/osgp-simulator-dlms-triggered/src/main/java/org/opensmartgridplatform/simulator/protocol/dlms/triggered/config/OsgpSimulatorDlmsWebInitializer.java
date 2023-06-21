// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.dlms.triggered.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;

/** Web application Java configuration class. */
public class OsgpSimulatorDlmsWebInitializer extends AbstractApplicationInitializer {

  private static final String SERVLET_NAME = "CXFServlet";
  private static final String SERVLET_MAPPING = "/*";

  public OsgpSimulatorDlmsWebInitializer() {
    super(ApplicationContext.class, "java:/comp/env/osgp/SimulatorDlmsTriggered/log-config");
  }

  /** Handles the startup of spring. */
  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    super.onStartup(servletContext);
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
