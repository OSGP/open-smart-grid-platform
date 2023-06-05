// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;

public class SwitchConfigurationBankRequest extends DeviceRequest {

  private final String configurationBank;

  public SwitchConfigurationBankRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final String configurationBank) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.configurationBank = configurationBank;
  }

  public SwitchConfigurationBankRequest(
      final Builder deviceRequestBuilder, final String configurationBank) {
    super(deviceRequestBuilder);
    this.configurationBank = configurationBank;
  }

  public String getConfigurationBank() {
    return this.configurationBank;
  }
}
