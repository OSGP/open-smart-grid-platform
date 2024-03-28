/*
 * Copyright 2022 Alliander N.V.
 */

package org.opensmartgridplatform.core.application.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class HealthController {

  private static final Logger LOGGER = LoggerFactory.getLogger(HealthController.class);

  private final JpaTransactionManager transactionManager;

  public HealthController(JpaTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @GetMapping("/ready")
  private Boolean ready() {
    LOGGER.info("Checking ready status");
    Connection connection;

    try {
      connection = Objects.requireNonNull(transactionManager.getDataSource()).getConnection();
      if (connection != null) {
        LOGGER.info("Connection is closed: " + connection.isClosed());
      } else {
        LOGGER.info("Connection is null");
      }

      return connection != null && !connection.isClosed();

    } catch (SQLException ignored) {
    }

    return false;
  }
}
