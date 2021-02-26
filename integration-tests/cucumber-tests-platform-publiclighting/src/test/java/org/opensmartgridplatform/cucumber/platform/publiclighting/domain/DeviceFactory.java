/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.publiclighting.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.opensmartgridplatform.cucumber.platform.helpers.DeviceType;
import org.opensmartgridplatform.cucumber.platform.helpers.Protocol;
import org.opensmartgridplatform.cucumber.platform.publiclighting.domain.platform.PlatformDeviceFactory;
import org.opensmartgridplatform.cucumber.platform.publiclighting.domain.protocol.ProtocolDeviceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceFactory implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceFactory.class);

    @Autowired
    PlatformDeviceFactory platformDeviceFactory;

    @Autowired
    ProtocolDeviceFactory protocolDeviceFactory;

    private final Map<DeviceType, BiConsumer<Protocol, Map<String, String>>> deviceFactoryMap = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        // this.deviceFactoryMap.put(DeviceType.SSLD, this::createSsld);
        this.deviceFactoryMap.put(DeviceType.LIGHT_MEASUREMENT_DEVICE, this::createLightMeasurementDevice);
        this.deviceFactoryMap.put(DeviceType.LIGHT_MEASUREMENT_GATEWAY, this::createLightMeasurementGateway);
    }

    public void createDevice(final DeviceType deviceType, final Protocol protocol, final Map<String, String> settings) {
        if (this.deviceFactoryMap.containsKey(deviceType)) {
            this.deviceFactoryMap.get(deviceType).accept(protocol, settings);
        } else {
            LOGGER.warn("Unsupported DeviceType: " + deviceType);
        }
    }

    // private void createSsld(final Protocol protocol, final Map<String,
    // String> settings) {
    //// this.platformDeviceFactory.createPlatformDevice(protocol,
    // DeviceType.SSLD, settings);
    // this.protocolDeviceFactory.createProtocolDevice(DeviceType.SSLD,
    // protocol, settings);
    // }

    private void createLightMeasurementDevice(final Protocol protocol, final Map<String, String> settings) {
        this.platformDeviceFactory.createPlatformDevice(DeviceType.LIGHT_MEASUREMENT_DEVICE, protocol, settings);
        this.protocolDeviceFactory.createProtocolDevice(DeviceType.LIGHT_MEASUREMENT_DEVICE, protocol, settings);
    }

    private void createLightMeasurementGateway(final Protocol protocol, final Map<String, String> settings) {
        this.platformDeviceFactory.createPlatformDevice(DeviceType.LIGHT_MEASUREMENT_GATEWAY, protocol, settings);
        this.protocolDeviceFactory.createProtocolDevice(DeviceType.LIGHT_MEASUREMENT_GATEWAY, protocol, settings);
    }

}
