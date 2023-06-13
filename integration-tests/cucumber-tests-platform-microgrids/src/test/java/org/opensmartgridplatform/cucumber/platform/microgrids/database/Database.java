// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.database;

import java.util.Arrays;
import java.util.List;
import org.opensmartgridplatform.adapter.protocol.iec61850.domain.repositories.Iec61850DeviceRepository;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.ws.WsMicrogridsNotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.cucumber.platform.microgrids.glue.steps.database.ws.WsMicrogridsResponseDataRepository;
import org.opensmartgridplatform.domain.core.repositories.RtuDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class Database {

  @Autowired private Iec61850DeviceRepository iec61850DeviceRepository;

  @Autowired private WsMicrogridsResponseDataRepository responseDataRepository;

  @Autowired
  private WsMicrogridsNotificationWebServiceConfigurationRepository
      notificationWebServiceConfigurationRepository;

  @Autowired
  @Qualifier("wsMicrogridsNotificationApplicationName")
  private String notificationApplicationName;

  @Autowired
  @Qualifier("wsMicrogridsNotificationMarshallerContextPath")
  private String notificationMarshallerContextPath;

  @Autowired
  @Qualifier("wsMicrogridsNotificationTargetUri")
  private String notificationTargetUri;

  @Autowired private RtuDeviceRepository rtuDeviceRepository;

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
