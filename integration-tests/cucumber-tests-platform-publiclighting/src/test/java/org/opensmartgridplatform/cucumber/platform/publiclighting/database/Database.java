// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.publiclighting.database;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Database {

  @Autowired
  @Qualifier("wsPublicLightingResponseDataRepository")
  private ResponseDataRepository responseDataRepository;

  @Autowired
  @Qualifier("wsPublicLightingNotificationWebServiceConfigurationRepository")
  private NotificationWebServiceConfigurationRepository
      notificationWebServiceConfigurationRepository;

  @Autowired
  @Qualifier("wsPublicLightingNotificationApplicationName")
  private String notificationApplicationName;

  @Autowired
  @Qualifier("wsPublicLightingNotificationMarshallerContextPath")
  private String notificationMarshallerContextPath;

  @Autowired
  @Qualifier("wsPublicLightingNotificationTargetUri")
  private String notificationTargetUri;

  private void insertDefaultData() {
    this.notificationWebServiceConfigurationRepository.saveAll(
        this.notificationEndpointConfigurations());
  }

  private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
    final NotificationWebServiceConfigurationBuilder builder =
        new NotificationWebServiceConfigurationBuilder()
            .withApplicationName(this.notificationApplicationName)
            .withMarshallerContextPath(this.notificationMarshallerContextPath)
            .withTargetUri(this.notificationTargetUri)
            .withoutCircuitBreakerConfig()
            .withoutKeyStoreConfig()
            .withoutTrustStoreConfig();

    final NotificationWebServiceConfiguration testOrgConfig =
        builder.withOrganisationIdentification("test-org").build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();

    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  @Transactional("txMgrWsPublicLighting")
  public void preparePublicLightingDatabaseForScenario() {
    this.responseDataRepository.deleteAll();
    this.notificationWebServiceConfigurationRepository.deleteAll();

    this.insertDefaultData();
  }
}
