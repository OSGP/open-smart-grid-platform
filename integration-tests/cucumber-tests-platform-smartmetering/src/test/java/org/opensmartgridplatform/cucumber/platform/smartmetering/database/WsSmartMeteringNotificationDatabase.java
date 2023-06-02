//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
