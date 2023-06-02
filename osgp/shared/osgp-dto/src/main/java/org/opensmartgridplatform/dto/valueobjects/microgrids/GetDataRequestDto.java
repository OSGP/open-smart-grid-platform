//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
