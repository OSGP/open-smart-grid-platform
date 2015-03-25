package com.alliander.osgp.domain.core.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.entities.Organisation;
import com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup;

@Repository
public interface DeviceAuthorizationRepository extends JpaRepository<DeviceAuthorization, Long> {
    List<DeviceAuthorization> findByDevice(Device device);

    List<DeviceAuthorization> findByDeviceAndFunctionGroup(Device device, DeviceFunctionGroup functionGroup);

    List<DeviceAuthorization> findByOrganisation(Organisation organisation);

    List<DeviceAuthorization> findByOrganisationAndDevice(Organisation organisation, Device device);

    @Query("select da from DeviceAuthorization da "
            + "where da.device = ?1 "
            + "and (?2 in "
            + "        (select da.organisation "
            + "         from DeviceAuthorization da "
            + "         where da.device = ?1 and da.functionGroup = com.alliander.osgp.domain.core.valueobjects.DeviceFunctionGroup.OWNER) "
            + "     or da.organisation = ?2)")
    List<DeviceAuthorization> findByDeviceForOrganisation(Device device, Organisation organisation);
}
