package com.alliander.osgp.core.db.api.application.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.core.db.api.entities.Device;
import com.alliander.osgp.core.db.api.repositories.DeviceDataRepository;
import com.alliander.osgp.dto.valueobjects.GpsCoordinates;

@Service
@Transactional(value = "osgpCoreDbApiTransactionManager", readOnly = true)
public class DeviceDataService {

    @Autowired
    private DeviceDataRepository deviceDataRepository;

    public Device findDevice(final String deviceIdentification) {

        return this.deviceDataRepository.findByDeviceIdentification(deviceIdentification);
    }

    public GpsCoordinates getGpsCoordinatesForDevice(final String deviceIdentification) {

        final Device device = this.findDevice(deviceIdentification);

        if (device != null) {
            return new GpsCoordinates(device.getGpsLatitude(), device.getGpsLongitude());
        }

        return null;
    }
}
