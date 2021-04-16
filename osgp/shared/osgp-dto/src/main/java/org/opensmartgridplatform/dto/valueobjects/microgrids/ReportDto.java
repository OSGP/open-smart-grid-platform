/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import org.joda.time.DateTime;

public class ReportDto extends ReportIdentifierDto {

  private static final long serialVersionUID = 4641800698416651986L;

  private final int sequenceNumber;
  private final DateTime timeOfEntry;

  public ReportDto(final int sequenceNumber, final DateTime timeOfEntry, final String id) {
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
