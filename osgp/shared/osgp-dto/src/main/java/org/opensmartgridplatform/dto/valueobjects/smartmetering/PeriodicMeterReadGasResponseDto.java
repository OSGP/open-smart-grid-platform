// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.dto.valueobjects.smartmetering;

import java.util.ArrayList;
import java.util.List;

public class PeriodicMeterReadGasResponseDto extends ActionResponseDto {

  private static final long serialVersionUID = -156966569210717654L;

  private final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReadsGas;
  private final PeriodTypeDto periodType;

  public PeriodicMeterReadGasResponseDto(
      final PeriodTypeDto periodType,
      final List<PeriodicMeterReadsGasResponseItemDto> periodicMeterReadsGas) {
    this.periodicMeterReadsGas = new ArrayList<>(periodicMeterReadsGas);
    this.periodType = periodType;
  }

  public List<PeriodicMeterReadsGasResponseItemDto> getPeriodicMeterReadsGas() {
    return new ArrayList<>(this.periodicMeterReadsGas);
  }

  public PeriodTypeDto getPeriodType() {
    return this.periodType;
  }
}
