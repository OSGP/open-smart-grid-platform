//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.iec61850.device.ssld.requests;

import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.CertificationDto;

public class UpdateDeviceSslCertificationDeviceRequest extends DeviceRequest {

  private CertificationDto certification;

  public UpdateDeviceSslCertificationDeviceRequest(
      final Builder deviceRequestBuilder, final CertificationDto certification) {
    super(deviceRequestBuilder);
    this.certification = certification;
  }

  public CertificationDto getCertification() {
    return this.certification;
  }
}
