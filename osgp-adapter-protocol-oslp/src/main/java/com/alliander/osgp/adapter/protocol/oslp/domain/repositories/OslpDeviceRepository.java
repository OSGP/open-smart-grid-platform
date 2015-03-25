package com.alliander.osgp.adapter.protocol.oslp.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.adapter.protocol.oslp.domain.entities.OslpDevice;

@Repository
public interface OslpDeviceRepository extends JpaRepository<OslpDevice, Long> {
    OslpDevice findByDeviceUid(String deviceUid);

    OslpDevice findByDeviceIdentification(String deviceIdentification);
}
