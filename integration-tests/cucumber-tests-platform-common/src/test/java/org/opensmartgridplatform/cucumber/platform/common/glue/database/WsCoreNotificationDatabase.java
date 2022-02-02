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

import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.CoreResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WsCoreNotificationDatabase extends WsNotificationDatabase {

  public WsCoreNotificationDatabase(
      final CoreResponseDataRepository responseDataRepository,
      final CoreResponseUrlDataRepository responseUrlDataRepository,
      final CoreNotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final CoreApplicationKeyConfigurationRepository applicationKeyConfigurationRepository,
      @Value("${web.service.notification.port}") final int webServiceNotificationPort,
      @Value("${web.service.notification.context}") final String webServiceNotificationContext) {
    super(
        "OSGP",
        String.format(
            "http://localhost:%s%s", webServiceNotificationPort, webServiceNotificationContext),
        false,
        "org.opensmartgridplatform.adapter.ws.schema.core.notification",
        responseDataRepository,
        responseUrlDataRepository,
        notificationWebServiceConfigurationRepository,
        applicationKeyConfigurationRepository);
  }

  @Override
  @Transactional(transactionManager = "txMgrWsCore")
  public void prepareDatabaseForScenario() {
    super.prepareDatabaseForScenario();
  }
}
