//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.microgrids.valueobjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GetDataRequest implements Serializable {

  private static final long serialVersionUID = 4776483459295815846L;

  private final List<SystemFilter> systemFilters;

  public GetDataRequest(final List<SystemFilter> systemFilters) {
    this.systemFilters = new ArrayList<>(systemFilters);
  }

  public List<SystemFilter> getSystemFilters() {
    return new ArrayList<>(this.systemFilters);
  }
}
