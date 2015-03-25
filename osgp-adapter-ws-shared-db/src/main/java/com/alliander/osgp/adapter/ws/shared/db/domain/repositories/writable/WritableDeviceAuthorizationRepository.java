package com.alliander.osgp.adapter.ws.shared.db.domain.repositories.writable;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

@Repository
public interface WritableDeviceAuthorizationRepository extends JpaRepository<DeviceAuthorization, Long> {
    List<DeviceAuthorization> findByDeviceAndFunctionGroup(Device device, DeviceFunctionGroup functionGroup);
}
