// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.repository;

import java.util.List;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEncryptedSecretRepository extends JpaRepository<DbEncryptedSecret, Long> {

  @Query(
      value =
          "SELECT es FROM DbEncryptedSecret es "
              + "JOIN FETCH es.encryptionKeyReference "
              + "WHERE es.deviceIdentification = :deviceIdentification "
              + "AND es.secretType IN (:secretTypes) "
              + "AND es.secretStatus = :secretStatus "
              + "ORDER BY es.creationTime DESC, es.id DESC")
  List<DbEncryptedSecret> findSecrets(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("secretTypes") List<SecretType> secretTypes,
      @Param("secretStatus") SecretStatus secretStatus);

  @Query(
      value =
          "SELECT count(es) FROM DbEncryptedSecret es "
              + "WHERE es.deviceIdentification = :deviceIdentification AND es.secretType = :secretType "
              + "AND es.secretStatus= :secretStatus")
  int getSecretCount(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("secretType") SecretType secretType,
      @Param("secretStatus") SecretStatus secretStatus);

  @Modifying
  @Query(
      value =
          "UPDATE DbEncryptedSecret es "
              + "SET es.secretStatus = 'WITHDRAWN'"
              + "WHERE es.deviceIdentification = :deviceIdentification"
              + " AND es.secretStatus = 'NEW'"
              + " AND es.secretType IN (:secretTypes)")
  int withdrawSecretsWithStatusNew(
      @Param("deviceIdentification") String deviceIdentification,
      @Param("secretTypes") List<SecretType> secretTypes);
}
