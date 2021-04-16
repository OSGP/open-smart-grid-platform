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
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SpecialDaysRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -5161574052268470981L;

  private final List<SpecialDay> specialDays;

  public SpecialDaysRequestData(final List<SpecialDay> specialDays) {
    super();
    this.specialDays = new ArrayList<>(specialDays);
  }

  public List<SpecialDay> getSpecialDays() {
    return new ArrayList<>(this.specialDays);
  }

  @Override
  public void validate() throws FunctionalException {
    // Validation not necessary
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_SPECIAL_DAYS;
  }
}
