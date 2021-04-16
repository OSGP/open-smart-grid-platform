/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
