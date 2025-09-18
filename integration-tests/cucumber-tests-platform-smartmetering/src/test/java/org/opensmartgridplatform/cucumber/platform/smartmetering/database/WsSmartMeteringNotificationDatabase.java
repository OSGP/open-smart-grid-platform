// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.database;

import org.opensmartgridplatform.cucumber.platform.common.glue.database.WsNotificationDatabase;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.WsSmartMeteringApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.WsSmartMeteringNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.WsSmartMeteringResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.ws.WsSmartMeteringResponseUrlDataRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class WsSmartMeteringNotificationDatabase extends WsNotificationDatabase {

  public WsSmartMeteringNotificationDatabase(
      final WsSmartMeteringResponseDataRepository responseDataRepository,
      final WsSmartMeteringResponseUrlDataRepository responseUrlDataRepository,
      final WsSmartMeteringNotificationWebServiceConfigurationRepository
          notificationWebServiceConfigurationRepository,
      final WsSmartMeteringApplicationKeyConfigurationRepository
          applicationKeyConfigurationRepository,
      @Qualifier("wsSmartMeteringNotificationApplicationName")
          final String notificationApplicationName,
      @Qualifier("wsSmartMeteringNotificationMarshallerContextPath")
          final String notificationMarshallerContextPath,
      @Qualifier("wsSmartMeteringNotificationTargetUri") final String notificationTargetUri,
      @Qualifier("wsSmartMeteringNotificationKeystoreUse") final boolean notificationKeystoreUse,
      @Qualifier("wsSmartMeteringNotificationKeystoreType") final String notificationKeystoreType,
      @Qualifier("wsSmartMeteringNotificationKeystoreLocation")
          final String notificationKeystoreLocation,
      @Qualifier("wsSmartMeteringNotificationKeystorePassword")
          final String notificationKeystorePassword,
      @Qualifier("wsSmartMeteringNotificationTruststoreUse")
          final boolean notificationTruststoreUse,
      @Qualifier("wsSmartMeteringNotificationTruststoreType")
          final String notificationTruststoreType,
      @Qualifier("wsSmartMeteringNotificationTruststoreLocation")
          final String notificationTruststoreLocation,
      @Qualifier("wsSmartMeteringNotificationTruststorePassword")
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
  @Transactional(transactionManager = "txMgrWsSmartMetering")
  public void prepareDatabaseForScenario() {
    super.prepareDatabaseForScenario();
  }
}
