/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.da.valueobjects;

import java.io.Serializable;
import org.joda.time.DateTime;

public class GetPQValuesPeriodicRequest implements Serializable {
  private static final long serialVersionUID = 4776483459295815846L;

  private final DateTime from;
  private final DateTime to;

  public GetPQValuesPeriodicRequest(final DateTime from, final DateTime to) {
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
