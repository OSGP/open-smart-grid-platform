package org.opensmartgridplatform.secretmgmt.application.repository;

import java.util.Date;

import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.opensmartgridplatform.secretmgmt.application.services.encryption.providers.EncryptionProviderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DbEncryptionKeyRepository extends JpaRepository<DbEncryptionKeyReference, Long> {
    @Query("SELECT ekr FROM DbEncryptionKeyReference ekr " +
            "WHERE ekr.encryptionProviderType = :ept " +
            "AND ekr.validFrom < :date AND (ekr.validTo IS NULL OR ekr.validTo > :date) " +
            "ORDER BY ekr.validFrom DESC")
    Page<DbEncryptionKeyReference> findByTypeAndValid(@Param("date") Date validDate,
            @Param("ept") EncryptionProviderType encryptionProviderType, Pageable pageable);
}
