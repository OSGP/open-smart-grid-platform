//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.services;

import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.repository.DbEncryptionKeyRepository;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EncryptionKeyReferenceCacheService {
  private final DbEncryptionKeyRepository keyRepository;
  private final EnumMap<EncryptionProviderType, List<DbEncryptionKeyReference>>
      encryptionKeyReferenceCache;

  public EncryptionKeyReferenceCacheService(final DbEncryptionKeyRepository keyRepository) {
    this.keyRepository = keyRepository;
    this.encryptionKeyReferenceCache = new EnumMap<>(EncryptionProviderType.class);
  }

  public DbEncryptionKeyReference getKeyByReference(
      final EncryptionProviderType encryptionProviderType, final String reference) {
    this.initCache();

    return this.encryptionKeyReferenceCache.get(encryptionProviderType).stream()
        .filter(keyReference -> keyReference.getReference().equals(reference))
        .findAny()
        .orElse(null);
  }

  public List<DbEncryptionKeyReference> findAllByTypeAndValid(
      final EncryptionProviderType encryptionProviderType, final Date date) {
    this.initCache();

    return this.encryptionKeyReferenceCache.get(encryptionProviderType).stream()
        .filter(keyReference -> this.isValid(keyReference, date))
        .sorted(
            Comparator.comparing(DbEncryptionKeyReference::getValidFrom, Comparator.reverseOrder()))
        .toList();
  }

  private boolean isValid(final DbEncryptionKeyReference encryptionKeyReference, final Date date) {
    return !encryptionKeyReference.getValidFrom().after(date)
        && (encryptionKeyReference.getValidTo() == null
            || encryptionKeyReference.getValidTo().after(date));
  }

  private void initCache() {
    if (!this.encryptionKeyReferenceCache.isEmpty()) {
      return;
    }
    final List<DbEncryptionKeyReference> encryptionKeyReferences = this.keyRepository.findAll();
    this.encryptionKeyReferenceCache.putAll(
        encryptionKeyReferences.stream()
            .collect(Collectors.groupingBy(DbEncryptionKeyReference::getEncryptionProviderType)));
  }
}
