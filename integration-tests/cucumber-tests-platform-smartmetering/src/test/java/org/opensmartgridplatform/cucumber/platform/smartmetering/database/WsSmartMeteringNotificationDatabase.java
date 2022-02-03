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

import org.opensmartgridplatform.adapter.ws.domain.repositories.ApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.database.WsNotificationDatabase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WsSmartMeteringNotificationDatabase extends WsNotificationDatabase {

  public WsSmartMeteringNotificationDatabase(
      @Qualifier("wsSmartMeteringResponseDataRepository")
          final ResponseDataRepository responseDataRepository,
      @Qualifier("wsSmartMeteringResponseUrlDataRepository")
          final ResponseUrlDataRepository responseUrlDataRepository,
      @Qualifier("wsSmartMeteringNotificationWebServiceConfigurationRepository")
          final NotificationWebServiceConfigurationRepository
              notificationWebServiceConfigurationRepository,
      @Qualifier("wsSmartMeteringApplicationKeyConfigurationRepository")
          final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository,
      @Qualifier("wsSmartMeteringNotificationApplicationName")
          final String notificationApplicationName,
      @Qualifier("wsSmartMeteringNotificationMarshallerContextPath")
          final String notificationMarshallerContextPath,
      @Qualifier("wsSmartMeteringNotificationTargetUri") final String notificationTargetUri) {
    super(
        notificationApplicationName,
        notificationTargetUri,
        false,
        notificationMarshallerContextPath,
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
