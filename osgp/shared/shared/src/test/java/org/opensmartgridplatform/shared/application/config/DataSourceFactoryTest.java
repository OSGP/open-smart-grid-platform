package org.opensmartgridplatform.shared.application.config;

import static org.assertj.core.api.Assertions.assertThat;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.net.URL;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Optional;
import javax.sql.DataSource;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
class DataSourceFactoryTest {

  private final TestableDataSourceFactory factory = new TestableDataSourceFactory();

  @Container
  private static final PostgreSQLContainer<?> postgreSQLContainer =
      new PostgreSQLContainer<>("postgres:12.4")
          .withDatabaseName("test_db")
          .withUsername("osp_admin")
          .withPassword("1234")
          .withTmpFs(Collections.singletonMap("/var/lib/postgresql/data", "rw"));

  @Test
  void shouldConnectToDefaultDataSource() throws SQLException {
    final Optional<DataSource> optionalDataSource = this.factory.getDataSource();

    assertThat(optionalDataSource).isPresent();
    final HikariDataSource dataSource = (HikariDataSource) optionalDataSource.get();
    assertThat(dataSource.getPoolName()).isEqualTo("default");
    dataSource.getConnection().isValid(5);
    assertThat(this.factory.originalJdbcUrl).isEqualTo("jdbc:postgresql://localhost:5432/test_db");
  }

  @Test
  void shouldConnectToNamedDataSource() throws SQLException {
    final String dataSourceName = "test";

    final Optional<DataSource> optionalDataSource = this.factory.getDataSource(dataSourceName);

    assertThat(optionalDataSource).isPresent();
    final HikariDataSource dataSource = (HikariDataSource) optionalDataSource.get();
    assertThat(dataSource.getPoolName()).isEqualTo(dataSourceName);
    dataSource.getConnection().isValid(5);
    assertThat(this.factory.originalJdbcUrl).isEqualTo("jdbc:postgresql://localhost:5432/test_db");
  }

  @Test
  void shouldReturnEmptyOnMissingConfig() {
    final Optional<DataSource> optionalDataSource = this.factory.getDataSource("non-existent");
    assertThat(optionalDataSource).isEmpty();
  }

  private static class TestableDataSourceFactory extends DataSourceFactory {

    String originalJdbcUrl;

    @Override
    @NotNull
    HikariConfig getConfiguration(final String dataSourceName, final URL url) {
      final HikariConfig configuration = super.getConfiguration(dataSourceName, url);
      // Replace JDBC Url with the one from the TestContainer
      this.originalJdbcUrl = configuration.getJdbcUrl();
      configuration.setJdbcUrl(postgreSQLContainer.getJdbcUrl());
      return configuration;
    }
  }
}
