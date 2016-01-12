package org.osgp.adapter.protocol.dlms.domain.repositories;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityKeyRepository extends JpaRepository<SecurityKey, Long> {
    SecurityKey findByDlmsDeviceAndSecurityKeyTypeAndValidToIsNull(DlmsDevice dlmsDevice,
            SecurityKeyType securityKeyType);
}
