package com.alliander.osgp.domain.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.ProtocolInfo;

@Repository
public interface ProtocolInfoRepository extends JpaRepository<ProtocolInfo, Long> {

    ProtocolInfo findByProtocolAndProtocolVersion(String protocol, String protocolVersion);
}
