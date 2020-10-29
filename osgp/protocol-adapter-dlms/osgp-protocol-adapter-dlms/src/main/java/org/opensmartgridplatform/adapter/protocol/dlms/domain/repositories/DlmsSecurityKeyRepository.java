/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.repositories;

import java.util.List;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @Deprecated with the introduction of secret-management, the security_key table has become obsolete.
 */
@Deprecated
@Repository
public interface DlmsSecurityKeyRepository extends JpaRepository<SecurityKey, Long> {

    @Query("SELECT s FROM SecurityKey s JOIN FETCH s.dlmsDevice WHERE s.securityKeyType = (:securityKeyType)")
    public List<SecurityKey> findBySecurityKeyType(@Param("securityKeyType") SecurityKeyType securityKeyType);

    SecurityKey findByDlmsDeviceAndSecurityKeyTypeAndValidToIsNull(DlmsDevice dlmsDevice,
            SecurityKeyType securityKeyType);

    SecurityKey findByDlmsDeviceAndSecurityKeyTypeAndValidToNotNull(DlmsDevice dlmsDevice,
            SecurityKeyType securityKeyType);

}