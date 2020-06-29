/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.repository;

import java.util.Date;

import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmanagement.application.services.encryption.EncryptionProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DbEncryptionKeyRepository extends JpaRepository<DbEncryptionKeyReference, Long> {
    @Query("SELECT ekr FROM DbEncryptionKeyReference ekr " + "WHERE ekr.encryptionProviderType = :ept "
            + "AND ekr.validFrom < :date AND (ekr.validTo IS NULL OR ekr.validTo > :date) "
            + "ORDER BY ekr.validFrom DESC")
    Page<DbEncryptionKeyReference> findByTypeAndValid(@Param("date") Date validDate,
            @Param("ept") EncryptionProviderType encryptionProviderType, Pageable pageable);
}
