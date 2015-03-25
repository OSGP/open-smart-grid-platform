package com.alliander.osgp.webdevicesimulator.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.webdevicesimulator.domain.entities.OslpLogItem;

@Repository
public interface OslpLogItemRepository extends JpaRepository<OslpLogItem, Long> {
    @Query("select o from OslpLogItem o order by o.modificationTime desc")
    List<OslpLogItem> findAllOrderByModificationTimeDesc();
}
