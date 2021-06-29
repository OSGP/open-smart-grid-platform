package org.opensmartgridplatform.metrics;

import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlets.HealthCheckServlet;
import javax.servlet.ServletContextEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Slf4j
public class HealthCheckServletContextListener extends HealthCheckServlet.ContextListener {

  @Autowired private HealthCheckRegistry healthCheckRegistry;

  @Override
  protected HealthCheckRegistry getHealthCheckRegistry() {
    return this.healthCheckRegistry;
  }

  @Override
  public void contextInitialized(final ServletContextEvent event) {
    log.info("-= autowiring the HealthCheckRegistry into the ServletContext =-");
    WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext())
        .getAutowireCapableBeanFactory()
        .autowireBean(this);

    super.contextInitialized(event);
  }
}
