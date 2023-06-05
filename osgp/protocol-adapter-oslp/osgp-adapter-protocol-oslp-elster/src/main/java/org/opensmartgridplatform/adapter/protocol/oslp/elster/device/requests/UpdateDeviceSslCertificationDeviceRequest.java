// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.CertificationDto;

public class UpdateDeviceSslCertificationDeviceRequest extends DeviceRequest {

  private final CertificationDto certification;

  public UpdateDeviceSslCertificationDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final int messagePriority,
      final CertificationDto certification) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);

    this.certification = certification;
  }

  public UpdateDeviceSslCertificationDeviceRequest(
      final Builder deviceRequestBuilder, final CertificationDto certification) {
    super(deviceRequestBuilder);
    this.certification = certification;
  }

  public CertificationDto getCertification() {
    return this.certification;
  }
}
