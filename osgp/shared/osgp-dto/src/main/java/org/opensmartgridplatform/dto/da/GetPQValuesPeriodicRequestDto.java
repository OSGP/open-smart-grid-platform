// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.da;

import java.io.Serializable;
import org.joda.time.DateTime;

public class GetPQValuesPeriodicRequestDto implements Serializable {
  private static final long serialVersionUID = 4776483459295815846L;

  private final DateTime from;
  private final DateTime to;

  public GetPQValuesPeriodicRequestDto(final DateTime from, final DateTime to) {
    this.from = from;
    this.to = to;
  }

  public DateTime getFrom() {
    return this.from;
  }

  public DateTime getTo() {
    return this.to;
  }
}
