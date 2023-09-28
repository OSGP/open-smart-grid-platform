// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * request periodic reads for E or GAS meter
 *
 * @author dev
 */
public class PeriodicMeterReadsQuery implements Serializable {

  private static final long serialVersionUID = -2483665562035897062L;

  private final PeriodType periodType;
  private final Instant beginDate;
  private final Instant endDate;
  private final boolean mbusDevice;
  private final String deviceIdentification;

  public PeriodicMeterReadsQuery(
      final PeriodType periodType,
      final Instant beginDate,
      final Instant endDate,
      final boolean mbusDevice) {
    this(periodType, beginDate, endDate, mbusDevice, "");
  }

  public PeriodicMeterReadsQuery(
      final PeriodType periodType,
      final Instant beginDate,
      final Instant endDate,
      final boolean mbusDevice,
      final String deviceIdentification) {
    Objects.requireNonNull(beginDate);
    Objects.requireNonNull(endDate);
    this.periodType = periodType;
    this.beginDate = beginDate;
    this.endDate = endDate;
    this.mbusDevice = mbusDevice;
    this.deviceIdentification = deviceIdentification;
  }

  public PeriodicMeterReadsQuery(
      final PeriodType periodType, final Instant beginDate, final Instant endDate) {
    this(periodType, beginDate, endDate, false, "");
  }

  public PeriodType getPeriodType() {
    return this.periodType;
  }

  public Instant getBeginDate() {
    return this.beginDate;
  }

  public Instant getEndDate() {
    return this.endDate;
  }

  public boolean isMbusDevice() {
    return this.mbusDevice;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
