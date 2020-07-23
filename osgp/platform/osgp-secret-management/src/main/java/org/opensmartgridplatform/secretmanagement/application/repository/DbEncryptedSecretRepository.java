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

import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEncryptedSecretRepository extends JpaRepository<DbEncryptedSecret, Long> {
    @Query(value = "SELECT es.id FROM encrypted_secret es "
            + "LEFT JOIN encryption_key_reference ekr ON es.encryption_key_reference_id = ekr.id "
            + "WHERE es.device_identification = :deviceIdentification AND es.secret_type = :secretType "
            + "AND ekr.valid_from < :date AND (ekr.valid_to IS NULL OR ekr.valid_to > :date) "
            + "ORDER BY es.creation_time DESC, es.id DESC LIMIT 1", nativeQuery = true)
    Long findIdOfValidMostRecent(@Param("deviceIdentification") String deviceIdentification,
            @Param("secretType") String secretType, @Param("date") Date validDate);

}
