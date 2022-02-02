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

import org.opensmartgridplatform.cucumber.platform.common.glue.database.WsNotificationDatabase;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.SmartMeteringResponseUrlDataRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WsSmartMeteringNotificationDatabase extends WsNotificationDatabase {

  public WsSmartMeteringNotificationDatabase(
      final SmartMeteringResponseDataRepository responseDataRepository,
      final SmartMeteringResponseUrlDataRepository responseUrlDataRepository,
      final SmartMeteringNotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final SmartMeteringApplicationKeyConfigurationRepository
          applicationKeyConfigurationRepository) {
    super(
        "SMART_METERS",
        "http://localhost:8089/notifications/",
        false,
        "org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification",
        responseDataRepository,
        responseUrlDataRepository,
        notificationWebServiceConfigurationRepository,
        applicationKeyConfigurationRepository);
  }

  @Override
  @Transactional(transactionManager = "txMgrWsSmartMetering")
  public void prepareDatabaseForScenario() {
    super.prepareDatabaseForScenario();
  }
}
