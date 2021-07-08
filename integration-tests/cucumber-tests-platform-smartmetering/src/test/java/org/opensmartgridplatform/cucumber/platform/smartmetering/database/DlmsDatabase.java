/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.database;

import static org.opensmartgridplatform.cucumber.platform.PlatformDefaults.DEFAULT_ORGANIZATION_IDENTIFICATION;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationDataLookupKey;
import org.opensmartgridplatform.adapter.ws.domain.entities.ApplicationKeyConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.entities.NotificationWebServiceConfiguration;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ApplicationKeyConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.NotificationWebServiceConfigurationRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseDataRepository;
import org.opensmartgridplatform.adapter.ws.domain.repositories.ResponseUrlDataRepository;
import org.opensmartgridplatform.adapter.ws.smartmetering.application.ApplicationConstants;
import org.opensmartgridplatform.cucumber.platform.common.glue.steps.database.ws.NotificationWebServiceConfigurationBuilder;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptedSecretRepository;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** DLMS related database steps. */
@Component
public class DlmsDatabase {

  @Autowired private DlmsDeviceRepository dlmsDeviceRepo;

  @Autowired private DbEncryptionKeyRepository encryptionKeyRepository;

  @Autowired private DbEncryptedSecretRepository secretRepository;

  @Autowired private ResponseDataRepository responseDataRepo;

  @Autowired private ResponseUrlDataRepository responseUrlDataRepo;

  @Autowired
  private NotificationWebServiceConfigurationRepository
      notificationWebServiceConfigurationRepository;

  @Autowired private ApplicationKeyConfigurationRepository applicationKeyConfigurationRepository;

  /**
   * This method is used to create default data not directly related to the specific tests. For
   * example: A default dlms gateway device.
   */
  private void insertDefaultData() {
    this.notificationWebServiceConfigurationRepository.saveAll(
        this.notificationEndpointConfigurations());
    this.applicationKeyConfigurationRepository.save(this.getDefaultApplicationKeyConfiguration());
  }

  private List<NotificationWebServiceConfiguration> notificationEndpointConfigurations() {
    final NotificationWebServiceConfigurationBuilder builder =
        new NotificationWebServiceConfigurationBuilder()
            .withApplicationName(ApplicationConstants.APPLICATION_NAME)
            .withMarshallerContextPath(
                "org.opensmartgridplatform.adapter.ws.schema.smartmetering.notification")
            .withTargetUri("http://localhost:8843/notifications")
            .withoutCircuitBreakerConfig();
    final NotificationWebServiceConfiguration testOrgConfig = builder.build();
    final NotificationWebServiceConfiguration noOrganisationConfig =
        builder.withOrganisationIdentification("no-organisation").build();
    return Arrays.asList(testOrgConfig, noOrganisationConfig);
  }

  private ApplicationKeyConfiguration getDefaultApplicationKeyConfiguration() {
    final ApplicationDataLookupKey applicationDataLookupKey =
        new ApplicationDataLookupKey(DEFAULT_ORGANIZATION_IDENTIFICATION, "SMART_METERS");
    return new ApplicationKeyConfiguration(
        applicationDataLookupKey,
        "/etc/osp/smartmetering/keys/application/smartmetering-rsa-public.key");
  }

  /** Before each scenario dlms related stuff needs to be removed. */
  @Transactional(transactionManager = "txMgrDlms")
  public void prepareDatabaseForScenario() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

    this.dlmsDeviceRepo.deleteAllInBatch();
    this.responseDataRepo.deleteAllInBatch();
    this.responseUrlDataRepo.deleteAllInBatch();

    this.secretRepository.deleteAllInBatch();
    this.encryptionKeyRepository.deleteAllInBatch();
    final DbEncryptionKeyReference jreEncryptionKey = this.getJreEncryptionKey(new Date());
    this.encryptionKeyRepository.save(jreEncryptionKey);

    this.insertDefaultData();
  }

  private DbEncryptionKeyReference getJreEncryptionKey(final Date now) {
    final DbEncryptionKeyReference jreEncryptionKey = new DbEncryptionKeyReference();
    jreEncryptionKey.setEncryptionProviderType(EncryptionProviderType.JRE);
    jreEncryptionKey.setReference("1");
    jreEncryptionKey.setValidFrom(now);
    jreEncryptionKey.setCreationTime(now);
    jreEncryptionKey.setModificationTime(now);
    jreEncryptionKey.setModifiedBy("DlmsDatabase (Cucumber)");
    jreEncryptionKey.setVersion(1L);
    return jreEncryptionKey;
  }
}
