//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

/**
 * request periodic reads for E meter
 *
 * @author dev
 */
public class PeriodicMeterReadsRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -5667197909843709650L;

  private final PeriodType periodType;
  private final Date beginDate;
  private final Date endDate;

  public PeriodicMeterReadsRequestData(
      final PeriodType periodType, final Date beginDate, final Date endDate) {
    this.periodType = periodType;
    this.beginDate = beginDate;
    this.endDate = endDate;
  }

  public PeriodType getPeriodType() {
    return this.periodType;
  }

  public Date getBeginDate() {
    return this.beginDate;
  }

  public Date getEndDate() {
    return this.endDate;
  }

  @Override
  public void validate() throws FunctionalException {
    // Validation not necessary
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.REQUEST_PERIODIC_METER_DATA;
  }
}
