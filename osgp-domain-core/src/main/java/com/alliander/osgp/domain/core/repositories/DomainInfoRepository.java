package com.alliander.osgp.domain.core.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.DomainInfo;

@Repository
public interface DomainInfoRepository extends JpaRepository<DomainInfo, Long> {

}
