/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetDataRequestDto implements Serializable {

  private static final long serialVersionUID = -2708314693698798777L;

  private List<SystemFilterDto> systemFilters;

  public GetDataRequestDto(final List<SystemFilterDto> systemFilters) {
    this.systemFilters = new ArrayList<>(systemFilters);
  }

  public List<SystemFilterDto> getSystemFilters() {
    return Collections.unmodifiableList(this.systemFilters);
  }
}
