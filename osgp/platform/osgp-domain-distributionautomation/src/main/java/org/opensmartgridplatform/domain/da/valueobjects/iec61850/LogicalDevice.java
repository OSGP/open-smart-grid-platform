/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.valueobjects.iec61850;

import java.io.Serializable;
import java.util.List;

public class LogicalDevice implements Serializable {
  private static final long serialVersionUID = 4776483459295843437L;

  private final String name;
  private List<LogicalNode> logicalNodes;

  public LogicalDevice(final String name, final List<LogicalNode> logicalNodes) {
    this.name = name;
    this.logicalNodes = logicalNodes;
  }

  public String getName() {
    return this.name;
  }

  public List<LogicalNode> getLogicalNodes() {
    return this.logicalNodes;
  }
}
