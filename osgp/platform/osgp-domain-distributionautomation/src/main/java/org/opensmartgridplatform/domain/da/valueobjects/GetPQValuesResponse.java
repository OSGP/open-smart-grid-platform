// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.da.valueobjects;

import java.io.Serializable;
import java.util.List;
import org.opensmartgridplatform.domain.da.valueobjects.iec61850.LogicalDevice;

public class GetPQValuesResponse implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5918246836566709515L;

  private final List<LogicalDevice> logicalDevices;

  public GetPQValuesResponse(final List<LogicalDevice> logicalDevices) {
    this.logicalDevices = logicalDevices;
  }

  public List<LogicalDevice> getLogicalDevices() {
    return this.logicalDevices;
  }
}
