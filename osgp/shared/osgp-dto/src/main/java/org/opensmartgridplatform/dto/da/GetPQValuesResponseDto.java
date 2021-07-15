/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
