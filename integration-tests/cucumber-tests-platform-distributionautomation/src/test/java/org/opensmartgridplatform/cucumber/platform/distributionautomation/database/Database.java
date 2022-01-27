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
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Database {

  @Autowired private ResponseDataRepository responseDataRepository;

  @Autowired
  private NotificationWebServiceConfigurationRepository
      notificationWebServiceConfigurationRepository;

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

  private void insertDefaultData() {
    this.notificationWebServiceConfigurationRepository.saveAll(
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
    this.responseDataRepository.deleteAll();

    // Remove from osgp_core
    this.rtuDeviceRepository.deleteAll();

    this.insertDefaultData();
  }
}
