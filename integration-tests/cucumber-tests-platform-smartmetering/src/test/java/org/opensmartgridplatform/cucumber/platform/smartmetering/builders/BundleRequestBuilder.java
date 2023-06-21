// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.builders;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.Actions;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.BundleRequest;

public class BundleRequestBuilder {

  private String deviceIdentification;
  private Actions actions;

  public BundleRequestBuilder withDeviceIdentification(final String deviceIdentification) {
    this.deviceIdentification = deviceIdentification;
    return this;
  }

  public BundleRequestBuilder withActions(final Actions actions) {
    this.actions = actions;
    return this;
  }

  public BundleRequest build() {
    final BundleRequest request = new BundleRequest();
    request.setDeviceIdentification(this.deviceIdentification);
    request.setActions(this.actions);
    return request;
  }
}
