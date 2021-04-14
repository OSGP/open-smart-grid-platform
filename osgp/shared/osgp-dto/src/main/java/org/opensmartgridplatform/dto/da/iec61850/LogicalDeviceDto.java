/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
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
