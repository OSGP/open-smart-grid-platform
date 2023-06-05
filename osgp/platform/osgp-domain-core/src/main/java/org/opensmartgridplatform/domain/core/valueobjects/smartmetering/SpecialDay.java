// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;

public class SpecialDay implements Serializable {

  private static final long serialVersionUID = 8009905447322732462L;

  private final CosemDate specialDayDate;

  private final int dayId;

  public SpecialDay(final CosemDate specialDayDate, final int dayId) {
    super();
    this.specialDayDate = new CosemDate(specialDayDate);
    this.dayId = dayId;
  }

  public CosemDate getSpecialDayDate() {
    return new CosemDate(this.specialDayDate);
  }

  public int getDayId() {
    return this.dayId;
  }
}
