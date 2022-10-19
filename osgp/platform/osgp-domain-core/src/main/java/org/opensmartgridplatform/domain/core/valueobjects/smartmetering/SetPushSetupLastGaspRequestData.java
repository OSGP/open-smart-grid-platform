/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import org.opensmartgridplatform.domain.core.valueobjects.DeviceFunction;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;

public class SetPushSetupLastGaspRequestData implements ActionRequest {

  private final PushSetupLastGasp pushSetupLastGasp;

  public SetPushSetupLastGaspRequestData(final PushSetupLastGasp pushSetupLastGasp) {
    this.pushSetupLastGasp = pushSetupLastGasp;
  }

  public PushSetupLastGasp getPushSetupLastGasp() {
    return this.pushSetupLastGasp;
  }

  @Override
  public void validate() throws FunctionalException {
    // No validation needed

  }

  @Override
  public DeviceFunction getDeviceFunction() {
    return DeviceFunction.SET_PUSH_SETUP_LAST_GASP;
  }
}
