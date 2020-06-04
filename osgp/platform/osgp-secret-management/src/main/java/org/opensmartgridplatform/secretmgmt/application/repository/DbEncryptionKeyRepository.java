package org.opensmartgridplatform.secretmgmt.application.repository;

import java.util.Date;

import org.opensmartgridplatform.secretmgmt.application.domain.DbEncryptionKeyReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DbEncryptionKeyRepository extends JpaRepository<DbEncryptionKeyReference, Long> {
    @Query("SELECT ekr FROM encryptionKeyReference ekr "+
            "WHERE ekr.validFrom < :date AND (ekr.validTo IS NULL OR ekr.validTo > :date) "+
            "ORDER BY ekr.validFrom DESC")
    //TODO consider using native query/caching if performance not good enough
    Page<DbEncryptionKeyReference> findValidOrderedByValidFrom(@Param("date") Date validDate, Pageable pageable);
}
