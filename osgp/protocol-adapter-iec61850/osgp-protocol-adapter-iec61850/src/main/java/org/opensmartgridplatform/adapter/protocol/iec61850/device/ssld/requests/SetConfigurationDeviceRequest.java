// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;

public class SetConfigurationDeviceRequest extends DeviceRequest {

  private ConfigurationDto configuration;

  public SetConfigurationDeviceRequest(
      final Builder deviceRequestBuilder, final ConfigurationDto configuration) {
    super(deviceRequestBuilder);
    this.configuration = configuration;
  }

  public ConfigurationDto getConfiguration() {
    return this.configuration;
  }
}
