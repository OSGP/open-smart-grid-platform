// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;

/**
 * request periodic reads for E or GAS meter
 *
 * @author dev
 */
public class PeriodicMeterReadsQuery implements Serializable {

  private static final long serialVersionUID = -2483665562035897062L;

  private final PeriodType periodType;
  private final Date beginDate;
  private final Date endDate;
  private final boolean mbusDevice;
  private final String deviceIdentification;

  public PeriodicMeterReadsQuery(
      final PeriodType periodType,
      final Date beginDate,
      final Date endDate,
      final boolean mbusDevice) {
    this(periodType, beginDate, endDate, mbusDevice, "");
  }

  public PeriodicMeterReadsQuery(
      final PeriodType periodType,
      final Date beginDate,
      final Date endDate,
      final boolean mbusDevice,
      final String deviceIdentification) {
    this.periodType = periodType;
    this.beginDate = new Date(beginDate.getTime());
    this.endDate = new Date(endDate.getTime());
    this.mbusDevice = mbusDevice;
    this.deviceIdentification = deviceIdentification;
  }

  public PeriodicMeterReadsQuery(
      final PeriodType periodType, final Date beginDate, final Date endDate) {
    this(periodType, beginDate, endDate, false, "");
  }

  public PeriodType getPeriodType() {
    return this.periodType;
  }

  public Date getBeginDate() {
    return new Date(this.beginDate.getTime());
  }

  public Date getEndDate() {
    return new Date(this.endDate.getTime());
  }

  public boolean isMbusDevice() {
    return this.mbusDevice;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }
}
