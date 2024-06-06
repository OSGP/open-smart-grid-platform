// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.List;

public class GetOutagesResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = 3953818299926960294L;

  private List<OutageDto> outages;

  public GetOutagesResponseDto(final List<OutageDto> outages) {
    this.outages = outages;
  }

  public List<OutageDto> getOutages() {
    return this.outages;
  }
}
