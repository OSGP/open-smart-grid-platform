//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.ConfigurationDto;

public class SetConfigurationDeviceRequest extends DeviceRequest {

  private final ConfigurationDto configuration;

  public SetConfigurationDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final ConfigurationDto configuration,
      final int messagePriority) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.configuration = configuration;
  }

  public SetConfigurationDeviceRequest(
      final Builder deviceRequestBuilder, final ConfigurationDto configuration) {
    super(deviceRequestBuilder);
    this.configuration = configuration;
  }

  public ConfigurationDto getConfiguration() {
    return this.configuration;
  }
}
