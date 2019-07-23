/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories;

import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;

public class Iec60870DeviceFactory {

    private static final String KEY_DEVICE_IDENTIFICATION = "device_identification";
    private static final String KEY_COMMON_ADDRESS = "common_address";
    private static final String KEY_PORT = "port";

    private static final String DEFAULT_DEVICE_IDENTIFICATION = "iec60870_device";
    private static final String DEFAULT_PORT = "2404";
    private static final String DEFAULT_COMMON_ADDRESS = "0";

    public static Iec60870Device createDefaultWith(final String deviceIdentification) {
        final Iec60870Device device = new Iec60870Device(deviceIdentification);
        device.setCommonAddress(Integer.parseInt(DEFAULT_COMMON_ADDRESS));
        device.setPort(Integer.parseInt(DEFAULT_PORT));
        return device;
    }

    public static Iec60870Device fromSettings(final Map<String, String> settings) {
        final Iec60870Device device = new Iec60870Device(getDeviceIdentificationOrDefault(settings));
        device.setCommonAddress(getCommonAddressOrDefault(settings));
        device.setPort(getPortOrDefault(settings));
        return device;
    }

    private static String getDeviceIdentificationOrDefault(final Map<String, String> settings) {
        return settings.getOrDefault(KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
    }

    private static int getCommonAddressOrDefault(final Map<String, String> settings) {
        return Integer.parseInt(settings.getOrDefault(KEY_COMMON_ADDRESS, DEFAULT_COMMON_ADDRESS));
    }

    private static int getPortOrDefault(final Map<String, String> settings) {
        return Integer.parseInt(settings.getOrDefault(KEY_PORT, DEFAULT_PORT));
    }
}
