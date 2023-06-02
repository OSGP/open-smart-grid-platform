//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.da.valueobjects;

import java.io.Serializable;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.PhysicalDevice;

public class GetDeviceModelResponse implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 4881970943059881408L;

  private final PhysicalDevice physicalDevice;

  public GetDeviceModelResponse(final PhysicalDevice physicalDevice) {
    this.physicalDevice = physicalDevice;
  }

  public PhysicalDevice getPhysicalDevice() {
    return this.physicalDevice;
  }
}
