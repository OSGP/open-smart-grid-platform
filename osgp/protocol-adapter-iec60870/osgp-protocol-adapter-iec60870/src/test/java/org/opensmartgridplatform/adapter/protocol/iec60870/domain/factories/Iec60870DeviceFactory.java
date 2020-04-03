/**
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.domain.factories;

import java.util.Map;
import java.util.Optional;

import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;

public class Iec60870DeviceFactory {

    public static final String KEY_DEVICE_IDENTIFICATION = "device_identification";
    public static final String KEY_DEVICE_TYPE = "device_type";
    public static final String KEY_GATEWAY_DEVICE_IDENTIFICATION = "gateway_device_identification";
    public static final String KEY_COMMON_ADDRESS = "common_address";
    public static final String KEY_PORT = "port";
    public static final String KEY_INFORMATION_OBJECT_ADDRESS = "device_address";

    public static final String DEFAULT_DEVICE_IDENTIFICATION = "iec60870_device";
    public static final String DEFAULT_DEVICE_TYPE = "DA_DEVICE";
    public static final String DEFAULT_PORT = "2404";
    public static final String DEFAULT_COMMON_ADDRESS = "0";

    public static Iec60870Device createDefaultWith(final String deviceIdentification) {
        final Iec60870Device device = new Iec60870Device(deviceIdentification);
        device.setCommonAddress(Integer.parseInt(DEFAULT_COMMON_ADDRESS));
        device.setPort(Integer.parseInt(DEFAULT_PORT));

        return device;
    }

    public static Iec60870Device fromSettings(final Map<String, String> settings) {
        final Iec60870Device device = new Iec60870Device(getDeviceIdentificationOrDefault(settings),
                getDeviceTypeOrDefault(settings));
        device.setCommonAddress(getCommonAddressOrDefault(settings));
        device.setPort(getPortOrDefault(settings));
        optionalGatewayDeviceIdentification(settings).ifPresent(device::setGatewayDeviceIdentification);
        optionalInformationObjectAddress(settings).ifPresent(device::setInformationObjectAddress);
        return device;
    }

    private static String getDeviceIdentificationOrDefault(final Map<String, String> settings) {
        return settings.getOrDefault(KEY_DEVICE_IDENTIFICATION, DEFAULT_DEVICE_IDENTIFICATION);
    }

    private static DeviceType getDeviceTypeOrDefault(final Map<String, String> settings) {
        final String value = settings.getOrDefault(KEY_DEVICE_TYPE, DEFAULT_DEVICE_TYPE);
        return DeviceType.valueOf(value);
    }

    private static Optional<String> optionalGatewayDeviceIdentification(final Map<String, String> settings) {
        final String value = settings.get(KEY_GATEWAY_DEVICE_IDENTIFICATION);
        return Optional.ofNullable(value);
    }

    private static int getCommonAddressOrDefault(final Map<String, String> settings) {
        return Integer.parseInt(settings.getOrDefault(KEY_COMMON_ADDRESS, DEFAULT_COMMON_ADDRESS));
    }

    private static int getPortOrDefault(final Map<String, String> settings) {
        return Integer.parseInt(settings.getOrDefault(KEY_PORT, DEFAULT_PORT));
    }

    private static Optional<Integer> optionalInformationObjectAddress(final Map<String, String> settings) {
        final String value = settings.get(KEY_INFORMATION_OBJECT_ADDRESS);
        if (value == null) {
            return Optional.empty();
        } else {
            return Optional.of(Integer.parseInt(value));
        }
    }
}
