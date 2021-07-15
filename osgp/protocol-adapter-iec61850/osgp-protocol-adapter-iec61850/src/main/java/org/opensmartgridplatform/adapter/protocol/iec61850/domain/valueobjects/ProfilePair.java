/*
 * Copyright 2014-2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import java.util.Date;

public class ProfilePair {

  private final Float[] values;
  private final Date[] times;

  public ProfilePair(final Float[] values, final Date[] times) {
    this.values = values;
    this.times = times;
  }

  public Float[] getValues() {
    return this.values;
  }

  public Date[] getTimes() {
    return this.times;
  }
}
