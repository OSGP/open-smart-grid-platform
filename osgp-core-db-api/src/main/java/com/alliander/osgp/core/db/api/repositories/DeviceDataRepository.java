package com.alliander.osgp.core.db.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.core.db.api.entities.Device;

@Repository
public interface DeviceDataRepository extends JpaRepository<Device, Long> {
    Device findByDeviceIdentification(String deviceIdentification);
}
