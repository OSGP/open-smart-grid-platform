// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.common.glue.database;

import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.WsCoreApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.WsCoreNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.WsCoreResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.WsCoreResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WsCoreNotificationDatabase extends WsNotificationDatabase {

  public WsCoreNotificationDatabase(
      final WsCoreResponseDataRepository responseDataRepository,
      final WsCoreResponseUrlDataRepository responseUrlDataRepository,
      final WsCoreNotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final WsCoreApplicationKeyConfigurationRepository applicationKeyConfigurationRepository,
      @Qualifier("wsCoreNotificationApplicationName") final String notificationApplicationName,
      @Qualifier("wsCoreNotificationTargetUri") final String notificationTargetUri,
      @Qualifier("wsCoreNotificationMarshallerContextPath")
          final String notificationMarshallerContextPath,
      @Qualifier("wsCoreNotificationKeystoreUse") final boolean notificationKeystoreUse,
      @Qualifier("wsCoreNotificationKeystoreType") final String notificationKeystoreType,
      @Qualifier("wsCoreNotificationKeystoreLocation") final String notificationKeystoreLocation,
      @Qualifier("wsCoreNotificationKeystorePassword") final String notificationKeystorePassword,
      @Qualifier("wsCoreNotificationTruststoreUse") final boolean notificationTruststoreUse,
      @Qualifier("wsCoreNotificationTruststoreType") final String notificationTruststoreType,
      @Qualifier("wsCoreNotificationTruststoreLocation")
          final String notificationTruststoreLocation,
      @Qualifier("wsCoreNotificationTruststorePassword")
          final String notificationTruststorePassword) {
    super(
        notificationApplicationName,
        notificationTargetUri,
        notificationKeystoreUse,
        notificationKeystoreType,
        notificationKeystoreLocation,
        notificationKeystorePassword,
        notificationTruststoreUse,
        notificationTruststoreType,
        notificationTruststoreLocation,
        notificationTruststorePassword,
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
