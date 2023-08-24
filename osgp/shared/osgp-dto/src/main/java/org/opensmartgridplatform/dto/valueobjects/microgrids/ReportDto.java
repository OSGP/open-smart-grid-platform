// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.time.ZonedDateTime;

public class ReportDto extends ReportIdentifierDto {

  private static final long serialVersionUID = 4641800698416651986L;

  private final int sequenceNumber;
  private final ZonedDateTime timeOfEntry;

  public ReportDto(final int sequenceNumber, final ZonedDateTime timeOfEntry, final String id) {
    super(id);
    this.sequenceNumber = sequenceNumber;
    this.timeOfEntry = timeOfEntry;
  }

  public int getSequenceNumber() {
    return this.sequenceNumber;
  }

  public ZonedDateTime getTimeOfEntry() {
    return this.timeOfEntry;
  }
}
