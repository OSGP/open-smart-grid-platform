package com.alliander.osgp.logging.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.logging.domain.entities.WebServiceMonitorLogItem;

@Repository
public interface WebServiceMonitorLogRepository extends JpaRepository<WebServiceMonitorLogItem, Long> {

}
