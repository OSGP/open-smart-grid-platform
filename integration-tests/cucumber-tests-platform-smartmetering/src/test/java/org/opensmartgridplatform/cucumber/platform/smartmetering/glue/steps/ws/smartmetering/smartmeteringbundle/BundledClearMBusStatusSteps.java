/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import io.cucumber.java.en.Given;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ClearMbusStatusRequest;

public class BundledClearMBusStatusSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a clear M-Bus status action$")
  public void theBundleRequestContainsAClearMBusStatusAction() {
    final ClearMbusStatusRequest action = new ClearMbusStatusRequest();

    this.addActionToBundleRequest(action);
  }
}
