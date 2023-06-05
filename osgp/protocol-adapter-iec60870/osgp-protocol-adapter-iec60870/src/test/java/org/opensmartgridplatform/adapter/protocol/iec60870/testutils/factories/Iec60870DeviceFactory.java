// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec60870.testutils.factories;

import java.util.HashMap;
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
  public static final String DEFAULT_DEVICE_TYPE = "DISTRIBUTION_AUTOMATION_DEVICE";
  public static final String DEFAULT_PORT = "2404";
  public static final String DEFAULT_COMMON_ADDRESS = "0";

  public static final String GATEWAY_DEVICE_IDENTIFICATION = "TEST-GATEWAY-1";
  public static final String LMD_1_DEVICE_IDENTIFICATION = "TEST-LMD-1";
  public static final String LMD_2_DEVICE_IDENTIFICATION = "TEST-LMD-2";
  public static final String LMD_1_IOA = "1";
  public static final String LMD_2_IOA = "2";

  public static Iec60870Device createDefaultWith(final String deviceIdentification) {
    final Iec60870Device device = new Iec60870Device(deviceIdentification);
    device.setCommonAddress(Integer.parseInt(DEFAULT_COMMON_ADDRESS));
    device.setPort(Integer.parseInt(DEFAULT_PORT));
    return device;
  }

  public static Iec60870Device createDistributionAutomationDevice(
      final String deviceIdentification) {
    final Iec60870Device device =
        new Iec60870Device(deviceIdentification, DeviceType.DISTRIBUTION_AUTOMATION_DEVICE);
    device.setCommonAddress(Integer.parseInt(DEFAULT_COMMON_ADDRESS));
    device.setPort(Integer.parseInt(DEFAULT_PORT));
    return device;
  }

  public static Iec60870Device createLightMeasurementDevice(
      final String deviceIdentification, final String gatewayDeviceIdentification) {
    final Iec60870Device device = new Iec60870Device(deviceIdentification, DeviceType.LIGHT_SENSOR);
    device.setGatewayDeviceIdentification(gatewayDeviceIdentification);
    return device;
  }

  public static Iec60870Device createLightMeasurementGatewayDevice(
      final String deviceIdentification) {
    final Iec60870Device device =
        new Iec60870Device(deviceIdentification, DeviceType.LIGHT_MEASUREMENT_RTU);
    device.setCommonAddress(Integer.parseInt(DEFAULT_COMMON_ADDRESS));
    device.setPort(Integer.parseInt(DEFAULT_PORT));
    return device;
  }

  public static Iec60870Device getGatewayDevice() {
    return fromSettings(getGatewayDeviceSettings());
  }

  public static Iec60870Device getLightMeasurementDevice1() {
    return fromSettings(getLightMeasurementDevice1Settings());
  }

  public static Iec60870Device getLightMeasurementDevice2() {
    return fromSettings(getLightMeasurementDevice2Settings());
  }

  public static Iec60870Device fromSettings(final Map<String, String> settings) {
    final Iec60870Device device =
        new Iec60870Device(
            getDeviceIdentificationOrDefault(settings), getDeviceTypeOrDefault(settings));
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

  private static Optional<String> optionalGatewayDeviceIdentification(
      final Map<String, String> settings) {
    final String value = settings.get(KEY_GATEWAY_DEVICE_IDENTIFICATION);
    return Optional.ofNullable(value);
  }

  private static int getCommonAddressOrDefault(final Map<String, String> settings) {
    return Integer.parseInt(settings.getOrDefault(KEY_COMMON_ADDRESS, DEFAULT_COMMON_ADDRESS));
  }

  private static int getPortOrDefault(final Map<String, String> settings) {
    return Integer.parseInt(settings.getOrDefault(KEY_PORT, DEFAULT_PORT));
  }

  private static Optional<Integer> optionalInformationObjectAddress(
      final Map<String, String> settings) {
    final String value = settings.get(KEY_INFORMATION_OBJECT_ADDRESS);
    if (value == null) {
      return Optional.empty();
    } else {
      return Optional.of(Integer.parseInt(value));
    }
  }

  private static Map<String, String> getGatewayDeviceSettings() {
    final Map<String, String> settings = new HashMap<>();
    settings.put(KEY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
    settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_MEASUREMENT_RTU.name());
    return settings;
  }

  private static Map<String, String> getLightMeasurementDevice1Settings() {
    final Map<String, String> settings = new HashMap<>();
    settings.put(KEY_DEVICE_IDENTIFICATION, LMD_1_DEVICE_IDENTIFICATION);
    settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_SENSOR.name());
    settings.put(KEY_GATEWAY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
    settings.put(KEY_INFORMATION_OBJECT_ADDRESS, LMD_1_IOA);
    return settings;
  }

  private static Map<String, String> getLightMeasurementDevice2Settings() {
    final Map<String, String> settings = new HashMap<>();
    settings.put(KEY_DEVICE_IDENTIFICATION, LMD_2_DEVICE_IDENTIFICATION);
    settings.put(KEY_DEVICE_TYPE, DeviceType.LIGHT_SENSOR.name());
    settings.put(KEY_GATEWAY_DEVICE_IDENTIFICATION, GATEWAY_DEVICE_IDENTIFICATION);
    settings.put(KEY_INFORMATION_OBJECT_ADDRESS, LMD_2_IOA);
    return settings;
  }
}
