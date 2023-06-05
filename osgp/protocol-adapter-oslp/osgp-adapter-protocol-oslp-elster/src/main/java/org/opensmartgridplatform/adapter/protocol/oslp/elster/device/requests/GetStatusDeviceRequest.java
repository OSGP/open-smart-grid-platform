// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.oslp.elster.device.requests;

import org.opensmartgridplatform.adapter.protocol.oslp.elster.device.DeviceRequest;
import org.opensmartgridplatform.dto.valueobjects.DomainTypeDto;

public class GetStatusDeviceRequest extends DeviceRequest {

  private final DomainTypeDto domainType;

  public GetStatusDeviceRequest(
      final String organisationIdentification,
      final String deviceIdentification,
      final String correlationUid,
      final DomainTypeDto domainType,
      final int messagePriority) {
    super(organisationIdentification, deviceIdentification, correlationUid, messagePriority);
    this.domainType = domainType;
  }

  public GetStatusDeviceRequest(
      final Builder deviceRequestBuilder, final DomainTypeDto domainType) {
    super(deviceRequestBuilder);
    this.domainType = domainType;
  }

  public DomainTypeDto getDomainType() {
    return this.domainType;
  }
}
