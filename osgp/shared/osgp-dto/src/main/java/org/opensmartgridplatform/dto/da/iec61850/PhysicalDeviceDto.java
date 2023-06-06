// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da.iec61850;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PhysicalDeviceDto implements Serializable {
  private static final long serialVersionUID = 9057348041709504311L;

  private final String id;
  private final List<LogicalDeviceDto> logicalDevices;

  public PhysicalDeviceDto(final String id, final List<LogicalDeviceDto> logicalDevices) {
    this.id = id;
    this.logicalDevices = logicalDevices;
  }

  public String getId() {
    return this.id;
  }

  public List<LogicalDeviceDto> getLogicalDevices() {
    return Collections.unmodifiableList(
        this.logicalDevices != null ? this.logicalDevices : new ArrayList<>());
  }
}
