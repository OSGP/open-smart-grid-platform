package com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.DeviceModel;
import com.alliander.osgp.domain.core.entities.Manufacturer;

@Repository
public interface WritableDeviceModelRepository extends JpaRepository<DeviceModel, Long> {
    DeviceModel findByManufacturerIdAndModelCode(Manufacturer manufacturerId, String modelCode);
    List<DeviceModel> findByManufacturerId(Manufacturer manufacturerId);
}
