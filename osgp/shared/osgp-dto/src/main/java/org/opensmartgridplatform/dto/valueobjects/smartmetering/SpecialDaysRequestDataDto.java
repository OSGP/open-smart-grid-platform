// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public class SpecialDaysRequestDataDto implements ActionRequestDto {

  private static final long serialVersionUID = 2733945253731054997L;

  private List<SpecialDayDto> specialDays;

  public SpecialDaysRequestDataDto(final List<SpecialDayDto> specialDays) {
    super();
    this.specialDays = new ArrayList<>(specialDays);
  }

  public List<SpecialDayDto> getSpecialDays() {
    return new ArrayList<>(this.specialDays);
  }
}
