//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;

public class SetDeviceVerificationKeyDeviceRequest extends DeviceRequest {

  private final String verificationKey;

  public SetDeviceVerificationKeyDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final String verificationKey) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.verificationKey = verificationKey;
  }

  public SetDeviceVerificationKeyDeviceRequest(
      final Builder deviceRequestBuilder, final String verificationKey) {
    super(deviceRequestBuilder);
    this.verificationKey = verificationKey;
  }

  public String getVerificationKey() {
    return this.verificationKey;
  }
}
