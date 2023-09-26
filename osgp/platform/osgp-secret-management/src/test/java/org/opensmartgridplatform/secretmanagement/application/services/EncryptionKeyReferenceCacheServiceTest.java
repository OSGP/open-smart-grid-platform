// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.services;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static org.opensmartgridplatform.shared.security.EncryptionProviderType.HSM;
import static org.opensmartgridplatform.shared.security.EncryptionProviderType.JRE;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
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
        .thenReturn(List.of(this.hsm1, this.hsm2, this.jre1, this.jre2));

    final DbEncryptionKeyReference dbEncryptionKeyReference =
        this.service.getKeyByReference(HSM, "HSM1");
    assertThat(dbEncryptionKeyReference).isEqualTo(this.hsm1);
  }

  @Test
  void getKeyByReferenceNotExist() {
    when(this.keyRepository.findAll())
        .thenReturn(List.of(this.hsm1, this.hsm2, this.jre1, this.jre2));

    final DbEncryptionKeyReference dbEncryptionKeyReference =
        this.service.getKeyByReference(HSM, "XXXXX");
    assertThat(dbEncryptionKeyReference).isNull();
  }

  @Test
  void findAllByTypeAndValid() {
    when(this.keyRepository.findAll())
        .thenReturn(List.of(this.hsm1, this.hsm2, this.jre1, this.jre2));

    assertThat(
            this.service.findAllByTypeAndValid(
                HSM,
                ZonedDateTime.ofInstant(this.hsm1.getValidFrom(), ZoneId.systemDefault())
                    .minus(1, ChronoUnit.MILLIS)
                    .toInstant()))
        .isEqualTo(List.of());
    assertThat(this.service.findAllByTypeAndValid(HSM, this.hsm1.getValidFrom()))
        .isEqualTo(List.of(this.hsm1));
    assertThat(this.service.findAllByTypeAndValid(HSM, this.hsm1.getValidTo()))
        .isEqualTo(List.of(this.hsm2));
    assertThat(this.service.findAllByTypeAndValid(HSM, this.hsm2.getValidFrom()))
        .isEqualTo(List.of(this.hsm2));
    assertThat(
            this.service.findAllByTypeAndValid(
                HSM,
                ZonedDateTime.ofInstant(this.hsm2.getValidTo(), ZoneId.systemDefault())
                    .plusSeconds(1)
                    .toInstant()))
        .isEqualTo(List.of());

    assertThat(
            this.service.findAllByTypeAndValid(
                JRE,
                ZonedDateTime.ofInstant(this.jre1.getValidFrom(), ZoneId.systemDefault())
                    .minusSeconds(1)
                    .toInstant()))
        .isEqualTo(List.of());
    assertThat(this.service.findAllByTypeAndValid(JRE, this.jre1.getValidFrom()))
        .isEqualTo(List.of(this.jre1));
    assertThat(this.service.findAllByTypeAndValid(JRE, this.jre1.getValidTo()))
        .isEqualTo(List.of(this.jre2));
    assertThat(this.service.findAllByTypeAndValid(JRE, this.jre2.getValidFrom()))
        .isEqualTo(List.of(this.jre2));
    assertThat(
            this.service.findAllByTypeAndValid(
                JRE,
                ZonedDateTime.ofInstant(this.jre2.getValidFrom(), ZoneId.systemDefault())
                    .plusDays(100)
                    .toInstant()))
        .isEqualTo(List.of(this.jre2));
  }

  private DbEncryptionKeyReference newDbEncryptionKeyReference(
      final String reference,
      final EncryptionProviderType encryptionProviderType,
      final int validFromDaysOffset,
      final Integer validToDaysOffset) {
    final DbEncryptionKeyReference dbEncryptionKeyReference = new DbEncryptionKeyReference();
    dbEncryptionKeyReference.setReference(reference);
    dbEncryptionKeyReference.setEncryptionProviderType(encryptionProviderType);
    dbEncryptionKeyReference.setValidFrom(
        LocalDate.now()
            .atStartOfDay()
            .atZone(ZoneId.systemDefault())
            .plusDays(validFromDaysOffset)
            .toInstant());
    if (validToDaysOffset != null) {
      dbEncryptionKeyReference.setValidTo(
          LocalDate.now()
              .atStartOfDay()
              .atZone(ZoneId.systemDefault())
              .plusDays(validToDaysOffset)
              .toInstant());
    }
    return dbEncryptionKeyReference;
  }
}
