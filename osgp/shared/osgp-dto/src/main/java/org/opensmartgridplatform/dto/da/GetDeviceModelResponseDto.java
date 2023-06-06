// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da;

import java.io.Serializable;
import org.opensmartgridplatform.dto.da.iec61850.PhysicalDeviceDto;

public class GetDeviceModelResponseDto implements Serializable {
  private static final long serialVersionUID = 4776483459295812759L;

  private final PhysicalDeviceDto physicalDevice;

  public GetDeviceModelResponseDto(final PhysicalDeviceDto physicalDevice) {
    this.physicalDevice = physicalDevice;
  }

  public PhysicalDeviceDto getPhysicalDevice() {
    return this.physicalDevice;
  }
}
