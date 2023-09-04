// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    encryptionKey1.setCreationTime(Instant.now());
    encryptionKey1.setReference("keyRef1");
    encryptionKey1.setEncryptionProviderType(EncryptionProviderType.HSM);
    encryptionKey1.setValidFrom(Instant.now().minus(60000, ChronoUnit.MILLIS));
    encryptionKey1.setVersion(1L);
    encryptionKey1 = this.entityManager.persist(encryptionKey1);
    DbEncryptionKeyReference encryptionKey2 = new DbEncryptionKeyReference();
    encryptionKey2.setCreationTime(Instant.now());
    encryptionKey2.setReference("keyRef2");
    encryptionKey2.setEncryptionProviderType(EncryptionProviderType.JRE);
    encryptionKey2.setValidFrom(Instant.now().minus(60000, ChronoUnit.MILLIS));
    encryptionKey2.setValidTo(Instant.now().plus(60000, ChronoUnit.MILLIS));
    encryptionKey2.setVersion(1L);
    encryptionKey2 = this.entityManager.persist(encryptionKey2);
    DbEncryptionKeyReference encryptionKey3 = new DbEncryptionKeyReference();
    encryptionKey3.setCreationTime(Instant.now());
    encryptionKey3.setReference("keyRef3");
    encryptionKey3.setEncryptionProviderType(EncryptionProviderType.JRE);
    encryptionKey3.setValidFrom(Instant.now().minus(3600000, ChronoUnit.MILLIS));
    encryptionKey3.setValidTo(Instant.now().minus(60000, ChronoUnit.MILLIS));
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
