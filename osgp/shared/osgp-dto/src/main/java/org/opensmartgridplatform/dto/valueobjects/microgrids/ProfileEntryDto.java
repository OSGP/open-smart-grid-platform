// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import java.time.ZonedDateTime;

public class ProfileEntryDto implements Serializable {

  private static final long serialVersionUID = 5633230544785706777L;

  private final int id;
  private final ZonedDateTime time;
  private final double value;

  public ProfileEntryDto(final int id, final ZonedDateTime time, final double value) {
    this.id = id;
    this.time = time;
    this.value = value;
  }

  public int getId() {
    return this.id;
  }

  public ZonedDateTime getTime() {
    return this.time;
  }

  public double getValue() {
    return this.value;
  }
}
