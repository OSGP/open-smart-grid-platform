package org.opensmartgridplatform.adapter.ws.smartmetering.application.metrics;

import com.codahale.metrics.health.HealthCheck;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DatabaseHealthCheck extends HealthCheck {

  @Resource(name = "getDataSourceCore")
  private DataSource getDataSourceCore;

  @Override
  protected Result check() throws Exception {
    log.info("-= running database health check =-");

    try (final Connection connection = this.getDataSourceCore.getConnection()) {
      final Statement st = connection.createStatement();
      try (final ResultSet rs = st.executeQuery("select 1")) {
        return Result.healthy();
      }

    } catch (final Exception e) {
      return Result.unhealthy(e.getMessage());
    }
  }
}
