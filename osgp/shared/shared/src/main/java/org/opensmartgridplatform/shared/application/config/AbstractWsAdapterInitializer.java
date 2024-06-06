// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.application.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

/** Web application Java configuration class. */
public abstract class AbstractWsAdapterInitializer extends AbstractApplicationInitializer {

  private static final String DISPATCHER_SERVLET_NAME = "spring-ws";
  private static final String DISPATCHER_SERVLET_MAPPING = "/*";

  /**
   * Constructs instance of ApplicationInitializer specific for webservice adapters.
   *
   * @param contextClass the class holding application specific Spring ApplicationContext
   * @param logConfig jndi property which points to logback configuration
   */
  protected AbstractWsAdapterInitializer(final Class<?> contextClass, final String logConfig) {
    super(contextClass, logConfig);
  }

  /**
   * Default startup of application context for webservice adapters which: - Performs default
   * startup - Setup WSDL handling - Initialize DispatchServlet
   *
   * @param servletContext Java servlet context as supplied by application server.
   * @throws ServletException Thrown when a servlet encounters difficulty.
   */
  @Override
  public void onStartup(final ServletContext servletContext) throws ServletException {
    super.onStartup(servletContext);

    final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setContextClass(AnnotationConfigWebApplicationContext.class);
    servlet.setTransformWsdlLocations(true);

    final ServletRegistration.Dynamic dispatcher =
        servletContext.addServlet(DISPATCHER_SERVLET_NAME, servlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);
  }

  /**
   * Custom startup of application context for webservice adapters which can be used to initialize
   * the servlet on a different mapping using a different name.
   *
   * @param servletContext Java servlet context as supplied by application server.
   * @param servletName The custom servlet name to be used instead of {@code
   *     DISPATCHER_SERVLET_NAME}.
   * @param servletMapping The custom servlet mapping to be used instead of {@code
   *     DISPATCHER_SERVLET_MAPPING}.
   * @throws ServletException Thrown when a servlet encounters difficulty.
   */
  protected void customStartUp(
      final ServletContext servletContext, final String servletName, final String servletMapping)
      throws ServletException {
    super.onStartup(servletContext);

    final MessageDispatcherServlet servlet = new MessageDispatcherServlet();
    servlet.setContextClass(AnnotationConfigWebApplicationContext.class);
    servlet.setTransformWsdlLocations(true);

    final ServletRegistration.Dynamic dispatcher = servletContext.addServlet(servletName, servlet);
    dispatcher.setLoadOnStartup(1);
    dispatcher.addMapping(servletMapping);
  }
}
