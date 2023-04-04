/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.shared.security.EncryptionProviderType.HSM;
import static org.opensmartgridplatform.shared.security.EncryptionProviderType.JRE;

import java.util.Arrays;
import java.util.Collections;
import org.joda.time.DateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;

@ExtendWith(MockitoExtension.class)
class EncryptionKeyReferenceCacheServiceTest {
  @Mock private DbEncryptionKeyRepository keyRepository;
  @InjectMocks private EncryptionKeyReferenceCacheService service;

  final DbEncryptionKeyReference hsm1 = this.newDbEncryptionKeyReference("HSM1", HSM, -10, -1);
  final DbEncryptionKeyReference hsm2 = this.newDbEncryptionKeyReference("HSM2", HSM, -1, 10);
  final DbEncryptionKeyReference jre1 = this.newDbEncryptionKeyReference("JRE1", JRE, -10, -1);
  final DbEncryptionKeyReference jre2 = this.newDbEncryptionKeyReference("JRE2", JRE, -1, null);

  @Test
  void getKeyByReference() {
    when(this.keyRepository.findAll())
        .thenReturn(Arrays.asList(this.hsm1, this.hsm2, this.jre1, this.jre2));

    final DbEncryptionKeyReference dbEncryptionKeyReference =
        this.service.getKeyByReference(HSM, "HSM1");
    assertThat(dbEncryptionKeyReference).isEqualTo(this.hsm1);
  }

  @Test
  void getKeyByReferenceNotExist() {
    when(this.keyRepository.findAll())
        .thenReturn(Arrays.asList(this.hsm1, this.hsm2, this.jre1, this.jre2));

    final DbEncryptionKeyReference dbEncryptionKeyReference =
        this.service.getKeyByReference(HSM, "XXXXX");
    assertThat(dbEncryptionKeyReference).isNull();
  }

  @Test
  void findAllByTypeAndValid() {
    when(this.keyRepository.findAll())
        .thenReturn(Arrays.asList(this.hsm1, this.hsm2, this.jre1, this.jre2));

    assertThat(
            this.service.findAllByTypeAndValid(
                HSM, new DateTime(this.hsm1.getValidFrom()).minusMillis(1).toDate()))
        .isEqualTo(Collections.emptyList());
    assertThat(this.service.findAllByTypeAndValid(HSM, this.hsm1.getValidFrom()))
        .isEqualTo(Collections.singletonList(this.hsm1));
    assertThat(this.service.findAllByTypeAndValid(HSM, this.hsm1.getValidTo()))
        .isEqualTo(Collections.singletonList(this.hsm2));
    assertThat(this.service.findAllByTypeAndValid(HSM, this.hsm2.getValidFrom()))
        .isEqualTo(Collections.singletonList(this.hsm2));
    assertThat(
            this.service.findAllByTypeAndValid(
                HSM, new DateTime(this.hsm2.getValidTo()).plusSeconds(1).toDate()))
        .isEqualTo(Collections.emptyList());

    assertThat(
            this.service.findAllByTypeAndValid(
                JRE, new DateTime(this.jre1.getValidFrom()).minusSeconds(1).toDate()))
        .isEqualTo(Collections.emptyList());
    assertThat(this.service.findAllByTypeAndValid(JRE, this.jre1.getValidFrom()))
        .isEqualTo(Collections.singletonList(this.jre1));
    assertThat(this.service.findAllByTypeAndValid(JRE, this.jre1.getValidTo()))
        .isEqualTo(Collections.singletonList(this.jre2));
    assertThat(this.service.findAllByTypeAndValid(JRE, this.jre2.getValidFrom()))
        .isEqualTo(Collections.singletonList(this.jre2));
    assertThat(
            this.service.findAllByTypeAndValid(
                JRE, new DateTime(this.jre2.getValidFrom()).plusDays(100).toDate()))
        .isEqualTo(Collections.singletonList(this.jre2));
  }

  private DbEncryptionKeyReference newDbEncryptionKeyReference(
      final String reference,
      final EncryptionProviderType encryptionProviderType,
      final int validFromDaysOffset,
      final Integer validToDaysOffset) {
    final DbEncryptionKeyReference dbEncryptionKeyReference = new DbEncryptionKeyReference();
    dbEncryptionKeyReference.setReference(reference);
    dbEncryptionKeyReference.setEncryptionProviderType(encryptionProviderType);
    dbEncryptionKeyReference.setValidFrom(new DateTime().plusDays(validFromDaysOffset).toDate());
    if (validToDaysOffset != null) {
      dbEncryptionKeyReference.setValidTo(new DateTime().plusDays(validToDaysOffset).toDate());
    }
    return dbEncryptionKeyReference;
  }
}
