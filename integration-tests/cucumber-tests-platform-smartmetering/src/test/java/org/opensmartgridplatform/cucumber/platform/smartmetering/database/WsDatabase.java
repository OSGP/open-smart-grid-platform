/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.database;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** WsSmartMetering me related database steps. */
@Component
public class WsDatabase {
  private static final String APPLICATION_NAME = "SMART_METERS";

  @Autowired private SmartMeteringResponseDataRepository smartMeteringResponseDataRepository;

  @Autowired private SmartMeteringResponseUrlDataRepository smartMeteringResponseUrlDataRepository;

  @Autowired
  private SmartMeteringNotificationWebServiceConfigurationRepository
      smartMeteringNotificationWebServiceConfigurationRepository;

  @Autowired
  private SmartMeteringApplicationKeyConfigurationRepository
      smartMeteringApplicationKeyConfigurationRepository;

  /**
   * This method is used to create default data not directly related to the specific tests. For
   * example: A default dlms gateway device.
   */
  private void insertDefaultData() {
    this.smartMeteringNotificationWebServiceConfigurationRepository.saveAll(
        this.notificationEndpointConfigurations());
  }

  private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
    final NotificationWebServiceConfigurationBuilder builder =
        new NotificationWebServiceConfigurationBuilder()
            .withApplicationName(APPLICATION_NAME)
            .withMarshallerContextPath(
                "org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification")
            .withTargetUri("http://localhost:8088/notifications")
            .withoutKeyStoreConfig()
            .withoutTrustStoreConfig()
            .withoutCircuitBreakerConfig();
    final NotificationWebServiceConfiguration testOrgConfig = builder.build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();
    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  /** Before each scenario dlms related stuff needs to be removed. */
  @Transactional(transactionManager = "txMgrWsSmartMetering")
  public void prepareDatabaseForScenario() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    this.smartMeteringResponseDataRepository.deleteAllInBatch();
    this.smartMeteringResponseUrlDataRepository.deleteAllInBatch();
    this.smartMeteringNotificationWebServiceConfigurationRepository.deleteAllInBatch();
    this.smartMeteringApplicationKeyConfigurationRepository.deleteAllInBatch();

    this.insertDefaultData();
  }
}
