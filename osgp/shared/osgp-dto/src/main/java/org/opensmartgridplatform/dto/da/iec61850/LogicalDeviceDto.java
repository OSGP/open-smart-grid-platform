// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da.iec61850;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogicalDeviceDto implements Serializable {
  private static final long serialVersionUID = 3263349435741609185L;

  private final String name;
  private final List<LogicalNodeDto> logicalNodes;

  public LogicalDeviceDto(final String name, final List<LogicalNodeDto> logicalNodes) {
    this.name = name;
    this.logicalNodes = logicalNodes;
  }

  public String getName() {
    return this.name;
  }

  public List<LogicalNodeDto> getLogicalNodes() {
    return Collections.unmodifiableList(
        this.logicalNodes != null ? this.logicalNodes : new ArrayList<>());
  }
}
