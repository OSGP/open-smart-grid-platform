/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.dto.valueobjects.microgrids;

import java.io.Serializable;
import org.joda.time.DateTime;

public class ProfileEntryDto implements Serializable {

  private static final long serialVersionUID = 5633230544785706777L;

  private int id;
  private DateTime time;
  private double value;

  public ProfileEntryDto(final int id, final DateTime time, final double value) {
    this.id = id;
    this.time = time;
    this.value = value;
  }

  public int getId() {
    return this.id;
  }

  public DateTime getTime() {
    return this.time;
  }

  public double getValue() {
    return this.value;
  }
}
