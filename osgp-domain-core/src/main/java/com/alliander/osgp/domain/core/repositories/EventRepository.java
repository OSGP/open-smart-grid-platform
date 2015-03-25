package com.alliander.osgp.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    List<Event> findByDevice(Device device);
}
