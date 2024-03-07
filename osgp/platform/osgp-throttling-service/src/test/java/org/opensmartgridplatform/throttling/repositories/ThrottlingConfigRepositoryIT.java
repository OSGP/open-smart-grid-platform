// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opensmartgridplatform.throttling.ThrottlingServiceApplication;
import org.opensmartgridplatform.throttling.entities.ThrottlingConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
@SpringBootTest(
    classes = ThrottlingServiceApplication.class,
    webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = ThrottlingConfigRepositoryIT.Initializer.class)
class ThrottlingConfigRepositoryIT {

  private static final int MAX_WAIT_FOR_HIGH_PRIO = 1000;

  @ClassRule
  private static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:12.4")
          .withDatabaseName("throttling_integration_test_db")
          .withUsername("osp_admin")
          .withPassword("1234")
          .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

  public static final String NULL = "NULL";

  static class Initializer
      implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(final ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
              "spring.datasource.driver-class-name=" + postgreSQLContainer.getDriverClassName(),
              "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
              "spring.datasource.username=" + postgreSQLContainer.getUsername(),
              "spring.datasource.password=" + postgreSQLContainer.getPassword(),
              "spring.jpa.show-sql=false",
              "max.wait.for.high.prio.in.ms=" + MAX_WAIT_FOR_HIGH_PRIO)
          .applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @Autowired private ThrottlingConfigRepository throttlingConfigRepository;

  @BeforeAll
  static void beforeAll() {
    postgreSQLContainer.start();
  }

  @AfterAll
  static void afterAll() {
    postgreSQLContainer.stop();
  }

  @ParameterizedTest
  @ValueSource(strings = {NULL, "name", ""})
  void testValidationName(final String name) {
    final ThrottlingConfig throttlingConfig = this.newThrottlingConfig(name);

    if (NULL.equals(name)) {
      assertThatThrownBy(() -> this.throttlingConfigRepository.save(throttlingConfig))
          .isInstanceOf(ConstraintViolationException.class);
    } else {
      final ThrottlingConfig saved = this.throttlingConfigRepository.save(throttlingConfig);
      assertThat(saved.getId()).isNotNull();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-2, -1, 0, 1})
  void testValidationMaxConcurrency(final int maxConcurrency) {
    final ThrottlingConfig throttlingConfig =
        this.newThrottlingConfig("maxConcurrency_" + maxConcurrency);
    throttlingConfig.setMaxConcurrency(maxConcurrency);

    if (maxConcurrency < -1) {
      assertThatThrownBy(() -> this.throttlingConfigRepository.save(throttlingConfig))
          .isInstanceOf(ConstraintViolationException.class);
    } else {
      final ThrottlingConfig saved = this.throttlingConfigRepository.save(throttlingConfig);
      assertThat(saved.getId()).isNotNull();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {-2, -1, 0, 1})
  void testValidationMaxNewConnections(final int maxNewConnections) {
    final ThrottlingConfig throttlingConfig =
        this.newThrottlingConfig("maxNewConnections_" + maxNewConnections);
    throttlingConfig.setMaxNewConnections(maxNewConnections);

    if (maxNewConnections < -1) {
      assertThatThrownBy(() -> this.throttlingConfigRepository.save(throttlingConfig))
          .isInstanceOf(ConstraintViolationException.class);
    } else {
      final ThrottlingConfig saved = this.throttlingConfigRepository.save(throttlingConfig);
      assertThat(saved.getId()).isNotNull();
    }
  }

  @ParameterizedTest
  @ValueSource(longs = {-2, -1, 0, 1})
  void testValidationMaxMaxNewConnectionsResetTimeInMs(final long maxNewConnectionsResetTimeInMs) {
    final ThrottlingConfig throttlingConfig =
        this.newThrottlingConfig(
            "maxNewConnectionsResetTimeInMs_" + maxNewConnectionsResetTimeInMs);
    throttlingConfig.setMaxNewConnectionsResetTimeInMs(maxNewConnectionsResetTimeInMs);

    if (maxNewConnectionsResetTimeInMs < 0) {
      assertThatThrownBy(() -> this.throttlingConfigRepository.save(throttlingConfig))
          .isInstanceOf(ConstraintViolationException.class);
    } else {
      final ThrottlingConfig saved = this.throttlingConfigRepository.save(throttlingConfig);
      assertThat(saved.getId()).isNotNull();
    }
  }

  @ParameterizedTest
  @ValueSource(longs = {-2, -1, 0, 1})
  void testValidationMaxNewConnectionsWaitTimeInMs(final long maxNewConnectionsWaitTimeInMs) {
    final ThrottlingConfig throttlingConfig =
        this.newThrottlingConfig("maxNewConnectionsWaitTimeInMs_" + maxNewConnectionsWaitTimeInMs);
    throttlingConfig.setMaxNewConnectionsWaitTimeInMs(maxNewConnectionsWaitTimeInMs);

    if (maxNewConnectionsWaitTimeInMs < 0) {
      assertThatThrownBy(() -> this.throttlingConfigRepository.save(throttlingConfig))
          .isInstanceOf(ConstraintViolationException.class);
    } else {
      final ThrottlingConfig saved = this.throttlingConfigRepository.save(throttlingConfig);
      assertThat(saved.getId()).isNotNull();
    }
  }

  private ThrottlingConfig newThrottlingConfig(final String name) {
    return new ThrottlingConfig(name, 0, 0, 0, 0);
  }
}
