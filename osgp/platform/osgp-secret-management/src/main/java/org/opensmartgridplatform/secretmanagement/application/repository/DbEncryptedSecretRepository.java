/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.repository;

import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEncryptedSecretRepository extends JpaRepository<DbEncryptedSecret, Long> {
    @Query(value = "SELECT es FROM DbEncryptedSecret es "
            + "WHERE es.device_identification = :deviceIdentification AND es.secret_type = :secretType "
            + "AND es.secretStatus= :secretStatus AND es.encryptionKeyReference.valid_from < current_time() "
            + "AND (es.encryptionKeyReference.valid_to IS NULL OR es.encryptionKeyReference.valid_to > current_time()) "
            + "ORDER BY es.creation_time DESC, es.id DESC")
    Page<DbEncryptedSecret> findSecrets(@Param("deviceIdentification") String deviceIdentification,
            @Param("secretType") SecretType secretType, @Param("secretStatus") SecretStatus secretStatus,
            Pageable pageable);

    @Query(value = "SELECT count(es) FROM DbEncryptedSecret es "
            + "WHERE es.device_identification = :deviceIdentification AND es.secret_type = :secretType "
            + "AND es.secretStatus= :secretStatus AND es.encryptionKeyReference.valid_from < current_time() "
            + "AND (es.encryptionKeyReference.valid_to IS NULL OR es.encryptionKeyReference.valid_to > current_time())")
    int getSecretCount(@Param("deviceIdentification") String deviceIdentification,
            @Param("secretType") SecretType secretType, @Param("secretStatus") SecretStatus secretStatus);
}
