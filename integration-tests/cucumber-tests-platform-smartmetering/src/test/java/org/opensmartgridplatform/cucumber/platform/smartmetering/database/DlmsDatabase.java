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
import org.javalite.activejdbc.Base;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.ApplicationConstants;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.database.device.DatabaseConnectionParameters;
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
        this.notificationWebServiceConfigurationRepository.saveAll(this.notificationEndpointConfigurations());
    }

    private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
        final NotificationWebServiceConfigurationBuilder builder = new NotificationWebServiceConfigurationBuilder()
                .withApplicationName(ApplicationConstants.APPLICATION_NAME)
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
    @Transactional(transactionManager = "txMgrDlms")
    public void prepareDatabaseForScenario() {

        this.dlmsDSecurityKeyRepo.deleteAllInBatch();
        this.dlmsDeviceRepo.deleteAllInBatch();
        this.responseDataRepo.deleteAllInBatch();
        this.responseUrlDataRepo.deleteAllInBatch();

        if (!Base.hasConnection()) {
            Base.open(DatabaseConnectionParameters.getDriver(), String.format("jdbc:postgresql://%s:%s/%s", DatabaseConnectionParameters.getHost(), DatabaseConnectionParameters.getPort(), DatabaseConnectionParameters.getDatabase()),
                    DatabaseConnectionParameters.getUser(), DatabaseConnectionParameters.getPassword());
        }
        Base.exec("DELETE FROM public.encrypted_secret;");
        Base.exec("DELETE FROM public.encryption_key_reference;");
        Base.exec("insert into public.encryption_key_reference (id, reference, encryption_provider_type, valid_from, valid_to, creation_time, modification_time, modified_by, version)" +
                "values (1, 1, 'JRE', '2019-06-17 09:25:46.000000', '2021-06-18 09:26:09.000000', '2019-06-18 09:26:23.000000', '2019-06-18 09:26:25.000000', 1, 1);");

        Base.close();

        this.insertDefaultData();
    }

}
