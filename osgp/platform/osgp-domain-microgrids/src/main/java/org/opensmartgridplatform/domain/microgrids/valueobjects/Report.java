// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.microgrids.valueobjects;

import org.joda.time.DateTime;

public class Report extends ReportIdentifier {

  private static final long serialVersionUID = 8619381327184505453L;

  private final int sequenceNumber;
  private final DateTime timeOfEntry;

  public Report(final int sequenceNumber, final DateTime timeOfEntry, final String id) {
    super(id);
    this.sequenceNumber = sequenceNumber;
    this.timeOfEntry = timeOfEntry;
  }

  public int getSequenceNumber() {
    return this.sequenceNumber;
  }

  public DateTime getTimeOfEntry() {
    return this.timeOfEntry;
  }
}
