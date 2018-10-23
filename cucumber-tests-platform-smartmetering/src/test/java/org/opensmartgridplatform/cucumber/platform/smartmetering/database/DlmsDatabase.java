/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.database;

import java.util.Arrays;
import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.smartmetering.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * DLMS related database steps.
 */
@Component
public class DlmsDatabase {

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepo;

    @Autowired
    private DlmsSecurityKeyRepository dlmsDSecurityKeyRepo;

    @Autowired
    private ResponseDataRepository responseDataRepo;

    @Autowired
    private ResponseUrlDataRepository responseUrlDataRepo;

    @Autowired
    private NotificationWebServiceConfigurationRepository notificationWebServiceConfigurationRepository;

    /**
     * This method is used to create default data not directly related to the
     * specific tests. For example: A default dlms gateway device.
     */
    private void insertDefaultData() {
        this.notificationWebServiceConfigurationRepository.save(this.notificationEndpointConfigurations());
    }

    private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
        final NotificationWebServiceConfigurationBuilder builder = new NotificationWebServiceConfigurationBuilder()
                .withApplicationName("")
                .withMarshallerContextPath("org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification")
                .withTargetUri("http://localhost:8843/notifications").withoutCircuitBreakerConfig();
        final NotificationWebServiceConfiguration testOrgConfig = builder.build();
        final NotificationWebServiceConfiguration noOrganisationConfig = builder
                .withOrganisationIdentification("no-organisation").build();
        return Arrays.asList(testOrgConfig, noOrganisationConfig);
    }

    /**
     * Before each scenario dlms related stuff needs to be removed.
     */
    @Transactional(transactionManager = "txMgrCore")
    public void prepareDatabaseForScenario() {

        this.dlmsDSecurityKeyRepo.deleteAllInBatch();
        this.dlmsDeviceRepo.deleteAllInBatch();
        this.responseDataRepo.deleteAllInBatch();
        this.responseUrlDataRepo.deleteAllInBatch();

        this.insertDefaultData();
    }

}
