// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.device.da.rtu;

import java.io.Serializable;
import org.opensmartgridplatform.adapter.protocol.iec61850.device.DeviceRequest;

public class DaDeviceRequest extends DeviceRequest {

  private Serializable request;

  public DaDeviceRequest(final Builder deviceRequestBuilder, final Serializable request) {
    super(deviceRequestBuilder);
    this.request = request;
  }

  public Serializable getRequest() {
    return this.request;
  }
}
