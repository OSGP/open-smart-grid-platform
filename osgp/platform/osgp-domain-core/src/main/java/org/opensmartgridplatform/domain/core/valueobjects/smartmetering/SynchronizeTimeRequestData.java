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
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

@Getter
@AllArgsConstructor
public class SynchronizeTimeRequestData implements Serializable, ActionRequest {

  private static final long serialVersionUID = -3773273540392997806L;

  private final String timeZone;

  @Override
  public void validate() throws FunctionalException {
    // no validation needed
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SYNCHRONIZE_TIME;
  }
}
