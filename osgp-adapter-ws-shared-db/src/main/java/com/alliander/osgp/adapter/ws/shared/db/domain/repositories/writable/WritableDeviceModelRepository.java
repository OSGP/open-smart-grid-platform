package com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;

@Repository
public interface WritableDeviceModelRepository extends JpaRepository<DeviceModel, Long> {
    DeviceModel findByManufacturer(Manufacturer manufacturerId);
}
