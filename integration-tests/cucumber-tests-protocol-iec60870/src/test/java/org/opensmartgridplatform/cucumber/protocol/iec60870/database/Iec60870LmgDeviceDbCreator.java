/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.protocol.iec60870.database;

import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.repositories.Iec60870DeviceRepository;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.helpers.ProtocolDeviceCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Iec60870LmgDeviceDbCreator implements ProtocolDeviceCreator<Iec60870Device> {

    private static final String KEY_DEVICE_IDENTIFICATION = "DeviceIdentification";
    private static final String KEY_COMMON_ADDRESS = "CommonAddress";
    private static final String KEY_PORT = "Port";

    private static final String DEFAULT_DEVICE_IDENTIFICATION = "LMG-1";
    private static final int DEFAULT_COMMON_ADDRESS = 1;
    private static final int DEFAULT_PORT = 2404;

    private static final org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType LIGHT_MEASUREMENT_GATEWAY = org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType.LIGHT_MEASUREMENT_GATEWAY;

    @Autowired
    private Iec60870DeviceRepository repository;

    @Override
    // @Transactional("txMgrIec60870")
    public Iec60870Device apply(final Map<String, String> settings) {
        final Iec60870Device device = new Iec60870Device(lmgDeviceIdentification(settings), LIGHT_MEASUREMENT_GATEWAY);
        device.setPort(port(settings));
        device.setCommonAddress(commonAddress(settings));
        return this.repository.save(device);
    }

    private static String lmgDeviceIdentification(final Map<String, String> settings) {
        return ReadSettingsHelper.getString(settings, KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
    }

    private static Integer port(final Map<String, String> settings) {
        return ReadSettingsHelper.getInteger(settings, KEY_PORT, DEFAULT_PORT);
    }

    private static Integer commonAddress(final Map<String, String> settings) {
        return ReadSettingsHelper.getInteger(settings, KEY_COMMON_ADDRESS, DEFAULT_COMMON_ADDRESS);
    }

}
