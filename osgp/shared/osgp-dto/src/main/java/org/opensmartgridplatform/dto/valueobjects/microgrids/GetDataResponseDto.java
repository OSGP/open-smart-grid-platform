// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetDataResponseDto implements Serializable {

  private static final long serialVersionUID = 5903337694184574498L;

  private List<GetDataSystemIdentifierDto> getDataSystemIdentifiers;

  private final ReportDto report;

  public GetDataResponseDto(
      final List<GetDataSystemIdentifierDto> getDataSystemIdentifiers, final ReportDto report) {
    this.getDataSystemIdentifiers = new ArrayList<>(getDataSystemIdentifiers);
    this.report = report;
  }

  public List<GetDataSystemIdentifierDto> getGetDataSystemIdentifiers() {
    return Collections.unmodifiableList(this.getDataSystemIdentifiers);
  }

  public ReportDto getReport() {
    return this.report;
  }
}
