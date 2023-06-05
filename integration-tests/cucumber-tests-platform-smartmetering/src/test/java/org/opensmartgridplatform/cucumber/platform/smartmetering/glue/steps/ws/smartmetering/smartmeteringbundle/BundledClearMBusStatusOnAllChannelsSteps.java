// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import io.cucumber.java.en.Given;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.ClearMBusStatusOnAllChannelsRequest;

public class BundledClearMBusStatusOnAllChannelsSteps extends BaseBundleSteps {

  @Given("^the bundle request contains a clear M-Bus status on all channels action$")
  public void theBundleRequestContainsAClearMBusStatusOnAllChannelsAction() {
    final ClearMBusStatusOnAllChannelsRequest action = new ClearMBusStatusOnAllChannelsRequest();

    this.addActionToBundleRequest(action);
  }
}
