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
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreResponseUrlDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** WsCore related database steps. */
@Component
public class WsCoreDatabase {

  @Value("${web.service.notification.context}")
  private String webServiceNotificationContext;

  @Value("${web.service.notification.port}")
  private int webServiceNotificationPort;

  @Autowired private CoreResponseDataRepository coreResponseDataRepository;

  @Autowired private CoreResponseUrlDataRepository coreResponseUrlDataRepository;

  @Autowired
  private CoreNotificationWebServiceConfigurationRepository
      coreNotificationWebServiceConfigurationRepository;

  @Autowired
  private CoreApplicationKeyConfigurationRepository coreApplicationKeyConfigurationRepository;

  /**
   * This method is used to create default data not directly related to the specific tests. For
   * example: A default dlms gateway device.
   */
  private void insertDefaultData() {
    this.coreNotificationWebServiceConfigurationRepository.saveAll(
        this.notificationEndpointConfigurations());
  }

  private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
    final NotificationWebServiceConfigurationBuilder builder =
        new NotificationWebServiceConfigurationBuilder()
            .withApplicationName("OSGP")
            .withMarshallerContextPath(
                "org.opensmartgridplatform.adapter.ws.schema.core.notification")
            .withTargetUri(
                String.format(
                    "http://localhost:%s%s",
                    this.webServiceNotificationPort, this.webServiceNotificationContext))
            .withoutKeyStoreConfig()
            .withoutTrustStoreConfig()
            .withoutCircuitBreakerConfig();
    final NotificationWebServiceConfiguration testOrgConfig = builder.build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();
    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  /** Before each scenario dlms related stuff needs to be removed. */
  @Transactional(transactionManager = "txMgrWsCore")
  public void prepareDatabaseForScenario() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    this.coreResponseDataRepository.deleteAllInBatch();
    this.coreNotificationWebServiceConfigurationRepository.deleteAllInBatch();
    this.coreResponseUrlDataRepository.deleteAllInBatch();

    this.insertDefaultData();
  }
}
