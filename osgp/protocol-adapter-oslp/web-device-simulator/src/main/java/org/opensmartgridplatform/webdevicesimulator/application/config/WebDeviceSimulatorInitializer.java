// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.application.config;

import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import org.opensmartgridplatform.shared.application.config.AbstractApplicationInitializer;
import org.springframework.web.servlet.DispatcherServlet;

/** Web application Java configuration class. */
public class WebDeviceSimulatorInitializer extends AbstractApplicationInitializer {

  private static final String DISPATCHER_SERVLET_NAME = "dispatcher";
  private static final String DISPATCHER_SERVLET_MAPPING = "/";

  /** Default constructor. */
  public WebDeviceSimulatorInitializer() {
    super(ApplicationContext.class, "java:comp/env/osgp/WebDeviceSimulator/log-config");
  }

  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    super.onStartup(servletContext);

    InternalLoggerFactory.setDefaultFactory(Slf4JLoggerFactory.INSTANCE);

    final ServletRegistration.Dynamic dispatcher =
        servletContext.addServlet(DISPATCHER_SERVLET_NAME, new DispatcherServlet(this.rootContext));
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);
  }
}
