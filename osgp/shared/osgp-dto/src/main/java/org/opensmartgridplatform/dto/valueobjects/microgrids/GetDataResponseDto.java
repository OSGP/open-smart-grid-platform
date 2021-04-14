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
