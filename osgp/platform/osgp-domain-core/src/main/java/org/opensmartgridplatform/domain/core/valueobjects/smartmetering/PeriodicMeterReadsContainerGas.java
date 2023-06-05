// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReadsContainerGas extends ActionResponse implements Serializable {

  private static final long serialVersionUID = -156966569210717654L;

  private final List<PeriodicMeterReadsGas> periodicMeterReadsGas;
  private final PeriodType periodType;

  public PeriodicMeterReadsContainerGas(
      final PeriodType periodType, final List<PeriodicMeterReadsGas> periodicMeterReadsGas) {
    this.periodicMeterReadsGas = new ArrayList<>(periodicMeterReadsGas);
    this.periodType = periodType;
  }

  public List<PeriodicMeterReadsGas> getPeriodicMeterReadsGas() {
    return new ArrayList<>(this.periodicMeterReadsGas);
  }

  public PeriodType getPeriodType() {
    return this.periodType;
  }
}
