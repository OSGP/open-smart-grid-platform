//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class GetFirmwareVersionQuery implements Serializable {

  private static final long serialVersionUID = -217306438695457044L;

  private final boolean mbusDevice;

  public GetFirmwareVersionQuery() {
    this(false);
  }

  public GetFirmwareVersionQuery(final boolean mbusDevice) {
    this.mbusDevice = mbusDevice;
  }

  public boolean isMbusDevice() {
    return this.mbusDevice;
  }
}
