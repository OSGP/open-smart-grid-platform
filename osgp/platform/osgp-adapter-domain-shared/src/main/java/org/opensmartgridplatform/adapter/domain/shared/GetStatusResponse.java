// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.shared;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceStatusMapped;
import org.opensmartgridplatform.shared.exceptionhandling.OsgpException;
import org.opensmartgridplatform.shared.infra.jms.ResponseMessageResultType;

public class GetStatusResponse {
  ResponseMessageResultType result;
  OsgpException osgpException;
  DeviceStatusMapped deviceStatusMapped;

  public GetStatusResponse() {
    // Empty constructor.
  }

  public ResponseMessageResultType getResult() {
    return this.result;
  }

  public void setResult(final ResponseMessageResultType result) {
    this.result = result;
  }

  public OsgpException getOsgpException() {
    return this.osgpException;
  }

  public void setOsgpException(final OsgpException osgpException) {
    this.osgpException = osgpException;
  }

  public DeviceStatusMapped getDeviceStatusMapped() {
    return this.deviceStatusMapped;
  }

  public void setDeviceStatusMapped(final DeviceStatusMapped deviceStatusMapped) {
    this.deviceStatusMapped = deviceStatusMapped;
  }
}
