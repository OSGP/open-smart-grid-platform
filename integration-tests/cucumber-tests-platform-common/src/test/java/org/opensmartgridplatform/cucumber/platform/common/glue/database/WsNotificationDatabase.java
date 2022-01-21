/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
import org.springframework.transaction.annotation.Transactional;

public class WsNotificationDatabase {
  private final String applicationName;
  private final String targetUri;
  private final boolean useKeyStore;
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
    this.useKeyStore = useKeyStore;
    this.marshallerContextPath = marshallerContextPath;
    this.responseDataRepository = responseDataRepository;
    this.responseUrlDataRepository = responseUrlDataRepository;
    this.notificationWebServiceConfigurationRepository =
        notificationWebServiceConfigurationRepository;
    this.applicationKeyConfigurationRepository = applicationKeyConfigurationRepository;
  }

  public void prepareDatabaseForScenario() {
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
    if (!this.useKeyStore) {
      builder.withoutKeyStoreConfig().withoutTrustStoreConfig();
    }
    final NotificationWebServiceConfiguration testOrgConfig = builder.build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();
    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }
}
