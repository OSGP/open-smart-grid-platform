package com.alliander.osgp.domain.core.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.OslpLogItem;

@Repository
public interface OslpLogItemRepository extends JpaRepository<OslpLogItem, Long> {
    Page<OslpLogItem> findByDeviceIdentification(String deviceIdentification, Pageable pagable);
}
