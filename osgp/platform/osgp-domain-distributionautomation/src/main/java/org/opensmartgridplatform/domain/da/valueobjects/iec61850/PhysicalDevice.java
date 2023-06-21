// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.valueobjects.iec61850;

import java.io.Serializable;
import java.util.List;

public class PhysicalDevice implements Serializable {
  private static final long serialVersionUID = 4776483459295843436L;

  private final String id;
  private List<LogicalDevice> logicalDevices;

  public PhysicalDevice(final String id, final List<LogicalDevice> logicalDevices) {
    this.id = id;
    this.logicalDevices = logicalDevices;
  }

  public String getId() {
    return this.id;
  }

  public List<LogicalDevice> getLogicalDevices() {
    return this.logicalDevices;
  }
}
