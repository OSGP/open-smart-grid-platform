package org.opensmartgridplatform.secretmgmt.application.repository;

import java.util.Date;

import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmgmt.application.domain.SecretType;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbEncryptedSecretRepository extends JpaRepository<DbEncryptedSecret, Long> {
    @Query("SELECT es FROM DbEncryptedSecret es JOIN es.encryptionKeyReference ekr "+
            "WHERE es.deviceIdentification = :deviceIdentification AND es.secretType = :secretType " +
            "AND ekr.encryptionProviderType = :encryptionType " +
            "AND ekr.validFrom < :date AND (ekr.validTo IS NULL OR ekr.validTo > :date)" +
            "ORDER BY es.creationTime DESC")
    Page<DbEncryptedSecret> findValidOrderedByCreationTime(@Param("deviceIdentification") String deviceIdentification,
            @Param("secretType") SecretType secretType,
            @Param("encryptionType")EncryptionProviderType encryptionProviderType,
            @Param("date") Date validDate, Pageable pageable);

}
