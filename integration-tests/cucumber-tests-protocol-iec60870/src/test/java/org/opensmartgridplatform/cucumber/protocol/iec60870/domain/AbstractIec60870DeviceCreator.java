// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.protocol.iec60870.domain;

import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.entities.Iec60870Device;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.valueobjects.DeviceType;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.helpers.ProtocolDeviceCreator;

public abstract class AbstractIec60870DeviceCreator
    implements ProtocolDeviceCreator<Iec60870Device> {

  private static final String KEY_DEVICE_IDENTIFICATION = "DeviceIdentification";
  private static final String KEY_GATEWAY_DEVICE_IDENTIFICATION = "GatewayDeviceIdentification";
  private static final String KEY_COMMON_ADDRESS = "CommonAddress";
  private static final String KEY_PORT = "Port";
  private static final String KEY_INFORMATION_OBJECT_ADDRESS = "InformationObjectAddress";

  private static final String DEFAULT_DEVICE_IDENTIFICATION = "IEC60870-DVC-1";
  private static final String DEFAULT_GATEWAY_DEVICE_IDENTIFICATION = "IEC60870-RTU-1";
  private static final int DEFAULT_COMMON_ADDRESS = 1;
  private static final int DEFAULT_PORT = 62404;
  private static final int DEFAULT_INFORMATION_OBJECT_ADDRESS = 1;

  protected abstract DeviceType deviceType();

  protected String deviceIdentification() {
    return DEFAULT_DEVICE_IDENTIFICATION;
  }

  protected String deviceIdentification(final Map<String, String> settings) {
    return ReadSettingsHelper.getString(
        settings, KEY_DEVICE_IDENTIFICATION, this.deviceIdentification());
  }

  protected String gatewayDeviceIdentification() {
    return DEFAULT_GATEWAY_DEVICE_IDENTIFICATION;
  }

  protected String gatewayDeviceIdentification(final Map<String, String> settings) {
    return ReadSettingsHelper.getString(
        settings, KEY_GATEWAY_DEVICE_IDENTIFICATION, this.gatewayDeviceIdentification());
  }

  protected int port() {
    return DEFAULT_PORT;
  }

  protected Integer port(final Map<String, String> settings) {
    return ReadSettingsHelper.getInteger(settings, KEY_PORT, this.port());
  }

  protected int commonAddress() {
    return DEFAULT_COMMON_ADDRESS;
  }

  protected Integer commonAddress(final Map<String, String> settings) {
    return ReadSettingsHelper.getInteger(settings, KEY_COMMON_ADDRESS, this.commonAddress());
  }

  protected int informationObjectAddress() {
    return DEFAULT_INFORMATION_OBJECT_ADDRESS;
  }

  protected Integer informationObjectAddress(final Map<String, String> settings) {
    return ReadSettingsHelper.getInteger(
        settings, KEY_INFORMATION_OBJECT_ADDRESS, this.informationObjectAddress());
  }
}
