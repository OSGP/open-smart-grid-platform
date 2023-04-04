/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.tomcat.util.buf.HexUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;

public class DbEncryptedSecretRepositoryIT extends AbstractRepositoryIT {
  @Autowired private DbEncryptedSecretRepository repository;

  private static final String DEVICE_IDENTIFICATION = "TestDevice00123";

  private DbEncryptionKeyReference dbEncryptionKeyReference;
  private DbEncryptedSecret dbEncryptedSecret;

  @BeforeEach
  public void persistTestData() {
    this.dbEncryptionKeyReference = new DbEncryptionKeyReference();
    this.dbEncryptionKeyReference.setCreationTime(new Date());
    this.dbEncryptionKeyReference.setReference("keyRef1");
    this.dbEncryptionKeyReference.setEncryptionProviderType(EncryptionProviderType.HSM);
    this.dbEncryptionKeyReference.setValidFrom(new Date(System.currentTimeMillis() - 60000));
    this.dbEncryptionKeyReference.setVersion(1L);
    this.dbEncryptionKeyReference = this.entityManager.persist(this.dbEncryptionKeyReference);
    final DbEncryptedSecret instance = new DbEncryptedSecret();
    instance.setDeviceIdentification(DEVICE_IDENTIFICATION);
    instance.setSecretType(SecretType.E_METER_AUTHENTICATION_KEY);
    instance.setSecretStatus(SecretStatus.ACTIVE);
    instance.setEncodedSecret(HexUtils.toHexString("$3cr3t".getBytes()));
    instance.setEncryptionKeyReference(this.dbEncryptionKeyReference);
    instance.setCreationTime(new Date());
    this.dbEncryptedSecret = this.entityManager.persist(instance);
    this.entityManager.flush();
  }

  @Test
  public void testSetup() {
    assertThat(this.repository.count()).isEqualTo(1);
  }

  @Test
  public void getSecretCount() {
    final Integer activeCount =
        this.repository.getSecretCount(
            this.dbEncryptedSecret.getDeviceIdentification(),
            this.dbEncryptedSecret.getSecretType(),
            SecretStatus.ACTIVE);
    assertThat(activeCount).isEqualTo(1);
  }

  @Test
  public void findSecrets() {
    final List<DbEncryptedSecret> secretsList =
        this.repository.findSecrets(
            this.dbEncryptedSecret.getDeviceIdentification(),
            Collections.singletonList(this.dbEncryptedSecret.getSecretType()),
            SecretStatus.ACTIVE);
    assertThat(secretsList).hasSize(1);
    assertThat(secretsList.iterator().next().getId()).isEqualTo(this.dbEncryptedSecret.getId());
  }

  @Test
  public void findSecretsOutdatedKeyRef() {
    final Date now = new Date();
    this.dbEncryptionKeyReference.setValidTo(now);
    this.dbEncryptionKeyReference = this.entityManager.persist(this.dbEncryptionKeyReference);

    DbEncryptionKeyReference newEncryptionKeyRef = new DbEncryptionKeyReference();
    newEncryptionKeyRef.setCreationTime(now);
    newEncryptionKeyRef.setReference("keyRef2");
    newEncryptionKeyRef.setEncryptionProviderType(EncryptionProviderType.HSM);
    newEncryptionKeyRef.setValidFrom(now);
    newEncryptionKeyRef.setVersion(1L);
    newEncryptionKeyRef = this.entityManager.persist(newEncryptionKeyRef);

    final List<DbEncryptedSecret> secretsList =
        this.repository.findSecrets(
            this.dbEncryptedSecret.getDeviceIdentification(),
            Collections.singletonList(this.dbEncryptedSecret.getSecretType()),
            SecretStatus.ACTIVE);
    assertThat(secretsList).hasSize(1);
    final DbEncryptedSecret secret = secretsList.get(0);
    assertThat(secret.getId()).isEqualTo(this.dbEncryptedSecret.getId());
    assertThat(secret.getEncryptionKeyReference().getId())
        .isEqualTo(this.dbEncryptionKeyReference.getId());
  }
}
