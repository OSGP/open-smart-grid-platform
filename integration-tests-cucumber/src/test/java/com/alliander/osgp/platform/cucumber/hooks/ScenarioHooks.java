/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.hooks;

import java.util.Arrays;
import java.util.List;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.DeviceAuthorization;
import com.alliander.osgp.domain.core.repositories.DeviceAuthorizationRepository;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.platform.cucumber.support.DeviceId;
import com.alliander.osgp.platform.cucumber.support.ServiceEndpoint;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;

/**
 * class for preparing specific scenarios
 *
 */
@PropertySource("file:/etc/osp/osgp-cucumber-response-data-smart-metering.properties")
public class ScenarioHooks {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScenarioHooks.class);

    private DeviceRepository deviceRepository;
    private DlmsDeviceRepository dlmsDeviceRepository;
    private DeviceAuthorizationRepository deviceAuthorizationRepository;

    @Autowired
    private DeviceId deviceId;

    @Autowired
    private ServiceEndpoint serviceEndpoint;

    @Value("${service.endpoint.host}")
    private String serviceEndpointHost;

    @Before()
    public void setServiceEndpoint() {
        this.serviceEndpoint.setServiceEndpoint(this.serviceEndpointHost);
    }

    @Before("@SLIM-218")
    public void beforeSlim218(final Scenario scenario) {

        LOGGER.info("Preparing scenario for @SLIM-218");

        LOGGER.info("Scenario name: {}", scenario.getName());

        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cucumber.xml");
        this.deviceRepository = context.getBean(DeviceRepository.class);
        this.dlmsDeviceRepository = context.getBean(DlmsDeviceRepository.class);
        this.deviceAuthorizationRepository = context.getBean(DeviceAuthorizationRepository.class);

        this.deleteDevicesFromPlatform(Arrays.asList("E0026000059790003", "G00XX561204926019"));

        LOGGER.info("Ready preparing scenario for @SLIM-218");

        context.close();
    }

    @After("@SLIM-511")
    public void activateDevice(final Scenario scenario) {
        LOGGER.info("Resetting database after runnign scenario @SLIM-511");
        this.setDeviceIsActivateState(this.deviceId.getDeviceIdE(), true);
        LOGGER.info("Database settings are reset after @SLIM-511");
    }

    private void deleteDevicesFromPlatform(final List<String> deviceIdList) {
        for (final String deviceId : deviceIdList) {
            this.deleteCoreRecords(deviceId);
            this.deleteDlmsRecords(deviceId);
        }
    }

    private void deleteCoreRecords(final String deviceId) {
        Device device = this.deviceRepository.findByDeviceIdentification(deviceId);

        if (device != null) {
            LOGGER.info("deleting device and device_authorisations" + deviceId);
            final List<DeviceAuthorization> devAuths = this.deviceAuthorizationRepository.findByDevice(device);
            for (final DeviceAuthorization devauth : devAuths) {
                this.deviceAuthorizationRepository.delete(devauth);
            }

            device = this.deviceRepository.findByDeviceIdentification(deviceId);
            if (device != null) {
                this.deviceRepository.delete(device);
            }
        }
    }

    private void deleteDlmsRecords(final String deviceId) {
        final DlmsDevice dlmsDevice = this.dlmsDeviceRepository.findByDeviceIdentification(deviceId);
        if (dlmsDevice != null) {
            LOGGER.info("deleting dlmsDevice and securityKeys..." + deviceId);
            this.dlmsDeviceRepository.delete(dlmsDevice);
        }
    }

    public void setDeviceIsActivateState(final String deviceId, final boolean isActiveState) {
        final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("cucumber.xml");
        this.deviceRepository = context.getBean(DeviceRepository.class);
        this.setDeviceIsActive(deviceId, isActiveState);
        context.close();
    }

    private void setDeviceIsActive(final String deviceId, final boolean newState) {
        final Device device = this.deviceRepository.findByDeviceIdentification(deviceId);
        if (device != null) {
            LOGGER.info("setting dlmsDevice.setActivated() to " + newState + " for device " + deviceId);
            device.setActive(newState);
            this.deviceRepository.save(device);
        } else {
            LOGGER.error("no such device " + deviceId);
        }
    }

}
