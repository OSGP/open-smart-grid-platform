package com.alliander.osgp.webdevicesimulator.domain.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.alliander.osgp.webdevicesimulator.domain.entities.Device;

/**
 * Repository for device entities
 */
public interface DeviceRepository extends JpaRepository<Device, Long> {

    @Query("SELECT d FROM Device d ORDER BY d.id ASC")
    List<Device> findAllOrderById();

    Device findByDeviceUid(String deviceUid);
}
