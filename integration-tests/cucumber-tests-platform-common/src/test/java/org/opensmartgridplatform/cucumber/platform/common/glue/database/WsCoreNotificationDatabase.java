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

import org.opensmartgridplatform.adapter.ws.domain.repositories.ApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WsCoreNotificationDatabase extends WsNotificationDatabase {

  public WsCoreNotificationDatabase(
      @Qualifier("wsCoreResponseDataRepository")
          final ResponseDataRepository responseDataRepository,
      @Qualifier("wsCoreResponseUrlDataRepository")
          final ResponseUrlDataRepository responseUrlDataRepository,
      @Qualifier("wsCoreNotificationWebServiceConfigurationRepository")
          final NotificationWebServiceConfigurationRepository
              notificationWebServiceConfigurationRepository,
      @Qualifier("wsCoreApplicationKeyConfigurationRepository")
          final ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository,
      @Qualifier("wsCoreNotificationApplicationName") final String notificationApplicationName,
      @Qualifier("wsCoreNotificationTargetUri") final String notificationTargetUri,
      @Qualifier("wsCoreNotificationMarshallerContextPath")
          final String notificationMarshallerContextPath) {
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
  @Transactional(transactionManager = "txMgrWsCore")
  public void prepareDatabaseForScenario() {
    super.prepareDatabaseForScenario();
  }
}
