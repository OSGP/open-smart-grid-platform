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

public class GetDataResponse implements Serializable {

  private static final long serialVersionUID = 7345936024521675762L;

  private final List<GetDataSystemIdentifier> getDataSystemIdentifiers;

  private final Report report;

  public GetDataResponse(
      final List<GetDataSystemIdentifier> getDataSystemIdentifiers, final Report report) {
    this.getDataSystemIdentifiers = new ArrayList<>(getDataSystemIdentifiers);
    this.report = report;
  }

  public List<GetDataSystemIdentifier> getGetDataSystemIdentifiers() {
    return new ArrayList<>(this.getDataSystemIdentifiers);
  }

  public Report getReport() {
    return this.report;
  }
}
