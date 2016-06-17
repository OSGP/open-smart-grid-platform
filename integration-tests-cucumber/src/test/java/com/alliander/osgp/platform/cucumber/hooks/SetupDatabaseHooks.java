/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;

@Component 
@Configuration
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class SetupDatabaseHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(SetupDatabaseHooks.class);
    
//    This repo may ne used for other usecases.
//    @Autowired 
//    private MeterResponseDataRepository meterResponseDataRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;
    
    public void deleteCoreAndDlmsDevice(final String deviceId) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        if (device != null) {
            this.deviceRepository.delete(device);
            LOGGER.info("deleting device ..." + deviceId);
        }
        
        DlmsDevice dlmsDevice = dlmsDeviceRepository.findByDeviceIdentification(deviceId);
        if (dlmsDevice != null) {
            this.dlmsDeviceRepository.delete(dlmsDevice);
            LOGGER.info("deleting device ..." + deviceId);
        }
        
    }
}
