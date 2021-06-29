/*
 * Copyright 2021 Alliander N.V.
 */

package org.opensmartgridplatform.metrics;

import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WebApplicationInitializer
    implements org.springframework.web.WebApplicationInitializer {

  @Override
  public void onStartup(final ServletContext container) throws ServletException {
    container.addListener(new MetricsServletContextListener());
    container.addListener(new HealthCheckServletContextListener());

    final ServletRegistration.Dynamic adminServlet =
        container.addServlet("dropwizard-admin-servlet", new AdminServlet());
    adminServlet.setLoadOnStartup(1);
    adminServlet.addMapping("/metrics");

    final ServletRegistration.Dynamic healthCheckServlet =
        container.addServlet("dropwizard-health-check-servlet", new HealthCheckServlet());
    healthCheckServlet.setLoadOnStartup(1);
    healthCheckServlet.addMapping("/metrics/health");
  }
}
