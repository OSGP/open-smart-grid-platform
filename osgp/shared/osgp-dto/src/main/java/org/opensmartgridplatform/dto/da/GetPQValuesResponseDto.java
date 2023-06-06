// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.opensmartgridplatform.dto.da.iec61850.LogicalDeviceDto;

public class GetPQValuesResponseDto implements Serializable {
  private static final long serialVersionUID = 4776483459295815734L;

  private final List<LogicalDeviceDto> logicalDevices;

  public GetPQValuesResponseDto(final List<LogicalDeviceDto> logicalDevices) {
    this.logicalDevices = logicalDevices;
  }

  public List<LogicalDeviceDto> getLogicalDevices() {
    return Collections.unmodifiableList(
        this.logicalDevices != null ? this.logicalDevices : new ArrayList<>());
  }
}
