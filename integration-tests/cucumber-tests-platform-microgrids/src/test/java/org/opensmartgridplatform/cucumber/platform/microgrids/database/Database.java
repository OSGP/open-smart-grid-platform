/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.microgrids.database;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Database {

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

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
            .withApplicationName("ZownStream")
            .withMarshallerContextPath(
                "org.opensmartgridplatform.adapter.ws.schema.microgrids.notification")
            .withoutCircuitBreakerConfig();

    final NotificationWebServiceConfiguration testOrgConfig =
        builder.withOrganisationIdentification("test-org").build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();

    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  public void prepareDatabaseForScenario() {
    // Then remove stuff from osgp_adapter_protocol_iec61850
    this.iec61850DeviceRepository.deleteAll();

    // Then remove stuff from the osgp_adapter_ws_microgrids
    this.responseDataRepository.deleteAll();

    // Now remove all from the core.
    this.rtuDeviceRepository.deleteAll();

    this.insertDefaultData();
  }
}
