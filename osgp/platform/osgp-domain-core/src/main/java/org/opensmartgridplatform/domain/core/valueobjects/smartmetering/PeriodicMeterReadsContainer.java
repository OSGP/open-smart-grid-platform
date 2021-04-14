/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReadsContainer extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717657L;
  private final PeriodType periodType;

  private final List<PeriodicMeterReads> periodicMeterReads;

  public PeriodicMeterReadsContainer(
      final PeriodType periodType, final List<PeriodicMeterReads> periodicMeterReads) {
    this.periodicMeterReads = periodicMeterReads;
    this.periodType = periodType;
  }

  public List<PeriodicMeterReads> getPeriodicMeterReads() {
    return new ArrayList<>(this.periodicMeterReads);
  }

  public PeriodType getPeriodType() {
    return this.periodType;
  }
}
