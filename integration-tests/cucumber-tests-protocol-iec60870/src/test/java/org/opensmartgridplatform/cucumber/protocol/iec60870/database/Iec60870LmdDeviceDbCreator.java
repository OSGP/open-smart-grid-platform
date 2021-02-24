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
public class Iec60870LmdDeviceDbCreator implements ProtocolDeviceCreator<Iec60870Device> {

    @Autowired
    private Iec60870DeviceRepository repository;

    private static final String KEY_DEVICE_IDENTIFICATION = "DeviceIdentification";
    private static final String KEY_GATEWAY_DEVICE_IDENTIFICATION = "GatewayDeviceIdentification";

    private static final String KEY_INFORMATION_OBJECT_ADDRESS = "InformationObjectAddress";

    private static final String DEFAULT_DEVICE_IDENTIFICATION = "LMD-1";
    private static final String DEFAULT_GATEWAY_DEVICE_IDENTIFICATION = "LMG-1";

    private static final int DEFAULT_INFORMATION_OBJECT_ADDRESS = 1;

    private static final org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType LIGHT_MEASUREMENT_DEVICE = org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType.LIGHT_MEASUREMENT_DEVICE;

    @Override
    // @Transactional("txMgrIec60870")
    public Iec60870Device apply(final Map<String, String> settings) {
        final Iec60870Device device = new Iec60870Device(lmdDeviceIdentification(settings), LIGHT_MEASUREMENT_DEVICE);
        device.setGatewayDeviceIdentification(lmdGatewayDeviceIdentification(settings));
        device.setInformationObjectAddress(informationObjectAddress(settings));
        // Field is not needed for light measurement device, but is currently
        // not nullable...
        device.setCommonAddress(0);

        return this.repository.save(device);
    }

    private static String lmdDeviceIdentification(final Map<String, String> settings) {
        return ReadSettingsHelper.getString(settings, KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
    }

    private static String lmdGatewayDeviceIdentification(final Map<String, String> settings) {
        return ReadSettingsHelper.getString(settings, KEY_GATEWAY_DEVICE_IDENTIFICATION,
                DEFAULT_GATEWAY_DEVICE_IDENTIFICATION);
    }

    private static Integer informationObjectAddress(final Map<String, String> settings) {
        return ReadSettingsHelper.getInteger(settings, KEY_INFORMATION_OBJECT_ADDRESS,
                DEFAULT_INFORMATION_OBJECT_ADDRESS);
    }

}
