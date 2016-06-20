/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import java.util.List;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

/**
 * helper class for AddDevice to provide database access. It is used to prepare the database beforehand, and test 
 * for the existence of specific records afterwards.
 *
 */
@Component 
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class AddDeviceHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddDeviceHooks.class);
    
    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;
    
    /**
     * delete the database records that were created in a earlier run.
     * @param deviceId
     */
    public void deleteCoreAndDlmsDevice(final String deviceId) {
        deleteCoreRecords(deviceId);
        deleteDlmsRecords(deviceId);
    }

    /**
     * check that the given device is inserted
     * @param deviceId
     * @return
     */
    public boolean testCoreDevice(final String deviceId) {
        Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        final  List<DeviceAuthorization> devAuths =  deviceAuthorizationRepository.findByDevice(device);
        return device != null && devAuths.size() > 0;
    }

    /**
     * check that the given dlms device is inserted
     * @param deviceId
     * @return
     */
    public boolean testDlmsDevice(final String deviceId) {
        DlmsDevice dlmsDevice = dlmsDeviceRepository.findByDeviceIdentification(deviceId);
        return dlmsDevice != null  && dlmsDevice.getSecurityKeys().size() > 0;
    }

    private void deleteCoreRecords(final String deviceId) {
        Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        
        if (device != null) {
            LOGGER.info("deleting device and device_authorisations" + deviceId);
            final  List<DeviceAuthorization> devAuths =  deviceAuthorizationRepository.findByDevice(device);
            for (DeviceAuthorization devauth : devAuths) {
                deviceAuthorizationRepository.delete(devauth);
            }
            
            device = this.deviceRepository.findByDeviceIdentification(deviceId);
            if (device != null) {
                this.deviceRepository.delete(device);
            }
        }
    }
    
    private void deleteDlmsRecords(final String deviceId) {
        DlmsDevice dlmsDevice = dlmsDeviceRepository.findByDeviceIdentification(deviceId);
        if (dlmsDevice != null) {
            LOGGER.info("deleting dlmsDevice and securityKeys..." + deviceId);
            this.dlmsDeviceRepository.delete(dlmsDevice);
        }
    }

    
}
