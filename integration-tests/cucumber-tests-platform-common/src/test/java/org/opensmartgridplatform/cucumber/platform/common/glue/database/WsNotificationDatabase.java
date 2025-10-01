// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.database;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.config.ws.NotificationWebServiceConnectionConfig;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsNotificationDatabase {

  private static final Logger LOGGER = LoggerFactory.getLogger(WsNotificationDatabase.class);

  private final String applicationName;
  private final String marshallerContextPath;
  private final NotificationWebServiceConnectionConfig connectionConfig;
  private final ResponseDataRepository responseDataRepository;
  private final ResponseUrlDataRepository responseUrlDataRepository;
  private final NotificationWebServiceConfigurationRepository
      notificationWebServiceConfigurationRepository;
  private final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository;

  public WsNotificationDatabase(
      final String applicationName,
      final String marshallerContextPath,
      final NotificationWebServiceConnectionConfig connectionConfig,
      final ResponseDataRepository responseDataRepository,
      final ResponseUrlDataRepository responseUrlDataRepository,
      final NotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository) {
    this.applicationName = applicationName;
    this.marshallerContextPath = marshallerContextPath;
    this.connectionConfig = connectionConfig;
    this.responseDataRepository = responseDataRepository;
    this.responseUrlDataRepository = responseUrlDataRepository;
    this.notificationWebServiceConfigurationRepository =
        notificationWebServiceConfigurationRepository;
    this.applicationKeyConfigurationRepository = applicationKeyConfigurationRepository;
  }

  protected void prepareDatabaseForScenario() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    this.responseDataRepository.deleteAllInBatch();
    this.responseUrlDataRepository.deleteAllInBatch();
    this.notificationWebServiceConfigurationRepository.deleteAllInBatch();
    this.applicationKeyConfigurationRepository.deleteAllInBatch();

    this.insertDefaultData();
  }

  private void insertDefaultData() {
    this.notificationWebServiceConfigurationRepository.saveAll(
        this.notificationEndpointConfigurations());
  }

  private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
    final NotificationWebServiceConfigurationBuilder builder =
        new NotificationWebServiceConfigurationBuilder()
            .withApplicationName(this.applicationName)
            .withMarshallerContextPath(this.marshallerContextPath)
            .withTargetUri(this.connectionConfig.notificationTargetUri())
            .withoutCircuitBreakerConfig();
    this.configureNotificationKeystore(builder);
    final NotificationWebServiceConfiguration testOrgConfig = builder.build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();
    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  protected void configureNotificationKeystore(
      final NotificationWebServiceConfigurationBuilder builder) {
    if (this.connectionConfig.notificationKeystoreUse()) {
      LOGGER.info(
          "Setting up notification keystore using type {} and location: {}",
          this.connectionConfig.notificationKeystoreType(),
          this.connectionConfig.notificationKeystoreLocation());
      builder.withKeyStoreConfig(
          this.connectionConfig.notificationKeystoreType(),
          this.connectionConfig.notificationKeystoreLocation(),
          this.connectionConfig.notificationKeystorePassword());
    } else {
      LOGGER.info("Setting up notification without keystore");
      builder.withoutKeyStoreConfig();
    }
    if (this.connectionConfig.notificationTruststoreUse()) {
      LOGGER.info(
          "Setting up notification truststore using type {} and location: {}",
          this.connectionConfig.notificationTruststoreType(),
          this.connectionConfig.notificationTruststoreLocation());
      builder.withTrustStoreConfig(
          this.connectionConfig.notificationTruststoreType(),
          this.connectionConfig.notificationTruststoreLocation(),
          this.connectionConfig.notificationTruststorePassword());
    } else {
      LOGGER.info("Setting up notification without truststore");
      builder.withoutTrustStoreConfig();
    }
  }
}
