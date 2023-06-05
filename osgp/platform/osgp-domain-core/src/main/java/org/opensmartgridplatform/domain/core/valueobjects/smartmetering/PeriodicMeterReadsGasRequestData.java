// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.Date;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

/**
 * request periodic reads for GAS meter
 *
 * @author dev
 */
public class PeriodicMeterReadsGasRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -7830158798159794041L;

  private final PeriodType periodType;
  private final Date beginDate;
  private final Date endDate;
  private final String deviceIdentification;

  public PeriodicMeterReadsGasRequestData(
      final PeriodType periodType,
      final Date beginDate,
      final Date endDate,
      final String deviceIdentification) {
    this.periodType = periodType;
    this.beginDate = beginDate;
    this.endDate = endDate;
    this.deviceIdentification = deviceIdentification;
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

  public String getDeviceIdentification() {
    return this.deviceIdentification;
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
