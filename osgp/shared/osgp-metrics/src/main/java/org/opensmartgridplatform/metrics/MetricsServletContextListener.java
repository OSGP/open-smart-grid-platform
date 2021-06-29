package org.opensmartgridplatform.metrics;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.servlets.MetricsServlet;
import javax.servlet.ServletContextEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Slf4j
public class MetricsServletContextListener extends MetricsServlet.ContextListener {

  @Autowired private MetricRegistry metricRegistry;

  @Override
  protected MetricRegistry getMetricRegistry() {
    return this.metricRegistry;
  }

  @Override
  public void contextInitialized(final ServletContextEvent event) {
    log.info("-= autowiring the MetricRegistry into the ServletContext =-");
    WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext())
        .getAutowireCapableBeanFactory()
        .autowireBean(this);

    super.contextInitialized(event);
  }
}
