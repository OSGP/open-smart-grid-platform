// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.ZonedDateTime;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class OutageDto implements Serializable {

  private static final long serialVersionUID = 3450617767283546874L;

  private final ZonedDateTime timestamp;
  private final Long duration;

  public OutageDto(final ZonedDateTime timestamp, final Long duration) {
    this.timestamp = timestamp;
    this.duration = duration;
  }
}
