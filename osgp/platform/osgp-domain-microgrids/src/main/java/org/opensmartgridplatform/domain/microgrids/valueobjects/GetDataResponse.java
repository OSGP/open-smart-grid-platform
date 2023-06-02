//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
