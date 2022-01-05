/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.repository;

import java.util.List;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEncryptedSecretRepository extends JpaRepository<DbEncryptedSecret, Long> {
  @Query(
      value =
          "SELECT es FROM DbEncryptedSecret es "
              + "WHERE es.deviceIdentification = :deviceIdentification AND es.secretType = :secretType "
              + "AND es.secretStatus= :secretStatus "
              + "ORDER BY es.creationTime DESC, es.id DESC")
  List<DbEncryptedSecret> findSecrets(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("secretType") SecretType secretType,
      @Param("secretStatus") SecretStatus secretStatus);

  @Query(
      value =
          "SELECT es FROM DbEncryptedSecret es "
              + "WHERE es.deviceIdentification = :deviceIdentification AND es.secretType = :secretType "
              + "AND es.secretStatus = :secretStatus AND es.encodedSecret = :encodedSecret "
              + "ORDER BY es.creationTime DESC, es.id DESC")
  List<DbEncryptedSecret> findSecretsWithSpecificEncodedSecret(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("secretType") SecretType secretType,
      @Param("secretStatus") SecretStatus secretStatus,
      @Param("encodedSecret") String encodedSecret);

  @Query(
      value =
          "SELECT count(es) FROM DbEncryptedSecret es "
              + "WHERE es.deviceIdentification = :deviceIdentification AND es.secretType = :secretType "
              + "AND es.secretStatus= :secretStatus")
  int getSecretCount(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("secretType") SecretType secretType,
      @Param("secretStatus") SecretStatus secretStatus);
}
