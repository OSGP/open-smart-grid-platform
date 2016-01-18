package org.osgp.adapter.protocol.dlms.domain.repositories;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityKeyRepository extends JpaRepository<SecurityKey, Long> {

    @Query("SELECT sk from SecurityKey sk WHERE sk.dlmsDevice = ?1 AND sk.securityKeyType = ?2 AND sk.validFrom <= current_date AND ( sk.validTo IS NULL OR sk.validTo > current_date )")
    SecurityKey findValidSecurityKey(DlmsDevice dlmsDevice, SecurityKeyType securityKeyType);
}
