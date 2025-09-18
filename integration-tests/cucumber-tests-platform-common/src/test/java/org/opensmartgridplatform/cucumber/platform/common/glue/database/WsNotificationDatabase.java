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
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsNotificationDatabase {

  private static final Logger LOGGER = LoggerFactory.getLogger(WsNotificationDatabase.class);

  private final String applicationName;
  private final String targetUri;
  private final boolean notificationKeystoreUse;
  private final String notificationKeystoreType;
  private final String notificationKeystoreLocation;
  private final String notificationKeystorePassword;
  private final boolean notificationTruststoreUse;
  private final String notificationTruststoreType;
  private final String notificationTruststoreLocation;
  private final String notificationTruststorePassword;
  private final String marshallerContextPath;
  private final ResponseDataRepository responseDataRepository;
  private final ResponseUrlDataRepository responseUrlDataRepository;
  private final NotificationWebServiceConfigurationRepository
      notificationWebServiceConfigurationRepository;
  private final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository;

  public WsNotificationDatabase(
      final String applicationName,
      final String targetUri,
      final boolean useKeyStore,
      final String marshallerContextPath,
      final ResponseDataRepository responseDataRepository,
      final ResponseUrlDataRepository responseUrlDataRepository,
      final NotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository) {
    this.applicationName = applicationName;
    this.targetUri = targetUri;
    this.marshallerContextPath = marshallerContextPath;
    this.responseDataRepository = responseDataRepository;
    this.responseUrlDataRepository = responseUrlDataRepository;
    this.notificationWebServiceConfigurationRepository =
        notificationWebServiceConfigurationRepository;
    this.applicationKeyConfigurationRepository = applicationKeyConfigurationRepository;
    this.notificationKeystoreUse = useKeyStore;
    this.notificationKeystoreType = null;
    this.notificationKeystoreLocation = null;
    this.notificationKeystorePassword = null;
    this.notificationTruststoreUse = false;
    this.notificationTruststoreType = null;
    this.notificationTruststoreLocation = null;
    this.notificationTruststorePassword = null;
  }

  public WsNotificationDatabase(
      final String applicationName,
      final String targetUri,
      final boolean notificationKeystoreUse,
      final String notificationKeystoreType,
      final String notificationKeystoreLocation,
      final String notificationKeystorePassword,
      final boolean notificationTruststoreUse,
      final String notificationTruststoreType,
      final String notificationTruststoreLocation,
      final String notificationTruststorePassword,
      final String marshallerContextPath,
      final ResponseDataRepository responseDataRepository,
      final ResponseUrlDataRepository responseUrlDataRepository,
      final NotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository) {
    this.applicationName = applicationName;
    this.targetUri = targetUri;
    this.notificationKeystoreType = notificationKeystoreType;
    this.notificationKeystoreLocation = notificationKeystoreLocation;
    this.notificationKeystorePassword = notificationKeystorePassword;
    this.notificationTruststoreUse = notificationTruststoreUse;
    this.notificationTruststoreType = notificationTruststoreType;
    this.notificationTruststoreLocation = notificationTruststoreLocation;
    this.notificationTruststorePassword = notificationTruststorePassword;
    this.notificationKeystoreUse = notificationKeystoreUse;
    this.marshallerContextPath = marshallerContextPath;
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
            .withTargetUri(this.targetUri)
            .withoutCircuitBreakerConfig();
    this.configureNotificationKeystore(builder);
    final NotificationWebServiceConfiguration testOrgConfig = builder.build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();
    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  protected void configureNotificationKeystore(
      final NotificationWebServiceConfigurationBuilder builder) {
    if (this.notificationKeystoreUse) {
      LOGGER.info(
          "Setting up notification keystore using type {} and location: {}",
          this.notificationKeystoreType,
          this.notificationKeystoreLocation);
      builder.withKeyStoreConfig(
          this.notificationKeystoreType,
          this.notificationKeystoreLocation,
          this.notificationKeystorePassword);
    } else {
      LOGGER.info("Setting up notification without keystore");
      builder.withoutKeyStoreConfig();
    }
    if (this.notificationTruststoreUse) {
      LOGGER.info(
          "Setting up notification truststore using type {} and location: {}",
          this.notificationTruststoreType,
          this.notificationTruststoreLocation);
      builder.withTrustStoreConfig(
          this.notificationTruststoreType,
          this.notificationTruststoreLocation,
          this.notificationTruststorePassword);
    } else {
      LOGGER.info("Setting up notification without truststore");
      builder.withoutTrustStoreConfig();
    }
  }
}
