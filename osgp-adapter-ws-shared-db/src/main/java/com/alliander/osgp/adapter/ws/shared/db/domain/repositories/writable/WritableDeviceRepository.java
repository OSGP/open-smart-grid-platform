package com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Device;

@Repository
public interface WritableDeviceRepository extends JpaRepository<Device, Long> {
    Device findByDeviceIdentification(String deviceIdentification);
}
