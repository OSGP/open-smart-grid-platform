/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.ComponentType;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalExceptionType;

public class SynchronizeTimeRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -4724182593235620894L;

  private static final int MIN_DEVIATION = -720;
  private static final int MAX_DEVIATION = 720;
  private static final int DEVIATION_NOT_SPECIFIED = 0x8000;

  private final int deviation;

  private final boolean dst;

  public SynchronizeTimeRequestData(final int deviation, final boolean dst) {
    this.deviation = deviation;
    this.dst = dst;
  }

  public int getDeviation() {
    return this.deviation;
  }

  public boolean isDst() {
    return this.dst;
  }

  @Override
  public void validate() throws FunctionalException {
    if (((this.deviation < MIN_DEVIATION) || (this.deviation > MAX_DEVIATION))
        && (this.deviation != DEVIATION_NOT_SPECIFIED)) {
      throw new FunctionalException(
          FunctionalExceptionType.VALIDATION_ERROR,
          ComponentType.DOMAIN_SMART_METERING,
          new IllegalArgumentException("Deviation not in range [-720..720]: " + this.deviation));
    }
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SYNCHRONIZE_TIME;
  }
}
