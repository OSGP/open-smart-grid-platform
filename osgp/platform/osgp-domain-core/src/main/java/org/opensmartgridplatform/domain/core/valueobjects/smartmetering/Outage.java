// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Outage implements Serializable {

  private static final long serialVersionUID = 3450617767283546874L;

  private final ZonedDateTime endTime;
  private final Long duration;

  public Outage(final ZonedDateTime endTime, final Long duration) {
    this.endTime = endTime;
    this.duration = duration;
  }
}
