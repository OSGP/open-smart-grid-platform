/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.database;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database.ws.DistributionAutomationNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database.ws.DistributionAutomationResponseDataRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Database {

  @Autowired
  private DistributionAutomationResponseDataRepository distributionAutomationResponseDataRepository;

  @Autowired
  private DistributionAutomationNotificationWebServiceConfigurationRepository
      distributionAutomationNotificationWebServiceConfigurationRepository;

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  private void insertDefaultData() {
    this.distributionAutomationNotificationWebServiceConfigurationRepository.saveAll(
        this.notificationEndpointConfigurations());
  }

  private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
    final NotificationWebServiceConfigurationBuilder builder =
        new NotificationWebServiceConfigurationBuilder()
            .withApplicationName("DISTRIBUTION_AUTOMATION")
            .withMarshallerContextPath(
                "org.opensmartgridplatform.adapter.ws.schema.distributionautomation.notification");

    final NotificationWebServiceConfiguration osgpOrganizationConfig =
        builder.withOrganisationIdentification("OSGP").build();

    return Arrays.asList(osgpOrganizationConfig);
  }

  public void prepareDatabaseForScenario() {
    // Removes all test related data from the various databases

    // Remove from osgp_adapter_ws_distributionautomation
    this.distributionAutomationResponseDataRepository.deleteAll();

    // Remove from osgp_core
    this.rtuDeviceRepository.deleteAll();

    this.insertDefaultData();
  }
}
