/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class UpdateFirmwareRequestData implements ActionRequest {

  private static final long serialVersionUID = 1537858643381805500L;

  private final String firmwareIdentification;

  public UpdateFirmwareRequestData(final String firmwareIdentification) {
    this.firmwareIdentification = firmwareIdentification;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed
  }

  public String getFirmwareIdentification() {
    return this.firmwareIdentification;
  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.UPDATE_FIRMWARE;
  }
}
