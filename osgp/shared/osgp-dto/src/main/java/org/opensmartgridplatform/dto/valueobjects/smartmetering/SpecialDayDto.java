// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecialDayDto implements Serializable {

  private static final long serialVersionUID = 1218914744657942413L;

  private final CosemDateDto specialDayDate;

  private final int dayId;

  public SpecialDayDto(final CosemDateDto specialDayDate, final int dayId) {
    super();
    this.specialDayDate = new CosemDateDto(specialDayDate);
    this.dayId = dayId;
  }

  public CosemDateDto getSpecialDayDate() {
    return new CosemDateDto(this.specialDayDate);
  }

  public int getDayId() {
    return this.dayId;
  }
}
