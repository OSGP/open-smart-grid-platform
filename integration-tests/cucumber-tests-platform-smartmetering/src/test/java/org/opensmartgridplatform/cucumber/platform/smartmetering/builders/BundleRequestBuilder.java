/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
