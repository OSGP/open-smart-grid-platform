// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da;

import java.io.Serializable;
import java.time.OffsetDateTime;

public class GetPQValuesPeriodicRequestDto implements Serializable {
  private static final long serialVersionUID = 4776483459295815846L;

  private final OffsetDateTime from;
  private final OffsetDateTime to;

  public GetPQValuesPeriodicRequestDto(final OffsetDateTime from, final OffsetDateTime to) {
    this.from = from;
    this.to = to;
  }

  public OffsetDateTime getFrom() {
    return this.from;
  }

  public OffsetDateTime getTo() {
    return this.to;
  }
}
