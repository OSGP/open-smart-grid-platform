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

import java.util.Date;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.beans.factory.annotation.Autowired;

public class DbEncryptionKeyRepositoryIT extends AbstractRepositoryIT {
  @Autowired DbEncryptionKeyRepository repository;

  @BeforeEach
  public void persistTestData() {
    DbEncryptionKeyReference encryptionKey1 = new DbEncryptionKeyReference();
    encryptionKey1.setCreationTime(new Date());
    encryptionKey1.setReference("keyRef1");
    encryptionKey1.setEncryptionProviderType(EncryptionProviderType.HSM);
    encryptionKey1.setValidFrom(new Date(System.currentTimeMillis() - 60000));
    encryptionKey1.setVersion(1L);
    encryptionKey1 = this.entityManager.persist(encryptionKey1);
    DbEncryptionKeyReference encryptionKey2 = new DbEncryptionKeyReference();
    encryptionKey2.setCreationTime(new Date());
    encryptionKey2.setReference("keyRef2");
    encryptionKey2.setEncryptionProviderType(EncryptionProviderType.JRE);
    encryptionKey2.setValidFrom(new Date(System.currentTimeMillis() - 60000));
    encryptionKey2.setValidTo(new Date(System.currentTimeMillis() + 60000));
    encryptionKey2.setVersion(1L);
    encryptionKey2 = this.entityManager.persist(encryptionKey2);
    DbEncryptionKeyReference encryptionKey3 = new DbEncryptionKeyReference();
    encryptionKey3.setCreationTime(new Date());
    encryptionKey3.setReference("keyRef3");
    encryptionKey3.setEncryptionProviderType(EncryptionProviderType.JRE);
    encryptionKey3.setValidFrom(new Date(System.currentTimeMillis() - 3600000));
    encryptionKey3.setValidTo(new Date(System.currentTimeMillis() - 60000));
    encryptionKey3.setVersion(1L);
    encryptionKey3 = this.entityManager.persist(encryptionKey3);
    this.entityManager.flush();
  }

  @Test
  public void findNoValidTo() {
    final List<DbEncryptionKeyReference> results =
        this.repository.findByTypeAndValid(EncryptionProviderType.HSM, new Date());
    assertThat(results.size()).isEqualTo(1);
    final DbEncryptionKeyReference keyReference = results.get(0);
    assertThat(keyReference).isNotNull();
    assertThat(keyReference.getId()).isNotNull();
    assertThat(keyReference.getEncryptionProviderType()).isEqualTo(EncryptionProviderType.HSM);
    assertThat(keyReference.getReference()).isEqualTo("keyRef1");
  }

  @Test
  public void findValidTo() {
    final List<DbEncryptionKeyReference> results =
        this.repository.findByTypeAndValid(EncryptionProviderType.JRE, new Date());
    assertThat(results.size()).isEqualTo(1);
    final DbEncryptionKeyReference keyReference = results.get(0);
    assertThat(keyReference).isNotNull();
    assertThat(keyReference.getId()).isNotNull();
    assertThat(keyReference.getEncryptionProviderType()).isEqualTo(EncryptionProviderType.JRE);
    assertThat(keyReference.getReference()).isEqualTo("keyRef2");
  }
}
