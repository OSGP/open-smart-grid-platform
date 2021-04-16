/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.repository;

import java.util.Date;
import java.util.List;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.shared.security.EncryptionProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DbEncryptionKeyRepository extends JpaRepository<DbEncryptionKeyReference, Long> {
  @Query(
      "SELECT ekr FROM DbEncryptionKeyReference ekr WHERE ekr.encryptionProviderType = :ept "
          + "AND ekr.validFrom < :date AND (ekr.validTo IS NULL OR ekr.validTo > :date) "
          + "ORDER BY ekr.validFrom DESC")
  List<DbEncryptionKeyReference> findByTypeAndValid(
      @Param("ept") EncryptionProviderType encryptionProviderType, @Param("date") Date validDate);

  @Query(
      "SELECT ekr FROM DbEncryptionKeyReference ekr WHERE ekr.encryptionProviderType = :ept "
          + "AND ekr.reference = :reference "
          + "ORDER BY ekr.validFrom DESC")
  DbEncryptionKeyReference findByTypeAndReference(
      @Param("ept") EncryptionProviderType encryptionProviderType,
      @Param("reference") String reference);
}
