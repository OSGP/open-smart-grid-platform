/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class SetRandomisationSettingsRequestBuilder {

  private int directAttach;
  private int randomisationStartWindow;
  private int multiplicationFactor;
  private int numberOfRetries;

  public SetRandomisationSettingsRequestBuilder fromParameterMap(
      final Map<String, String> parameters) {
    this.directAttach = Integer.parseInt(parameters.get(PlatformKeys.KEY_DIRECT_ATTACH));
    this.randomisationStartWindow =
        Integer.parseInt(parameters.get(PlatformKeys.KEY_RANDOMISATION_START_WINDOW));
    this.multiplicationFactor =
        Integer.parseInt(parameters.get(PlatformKeys.KEY_MULTIPLICATION_FACTOR));
    this.numberOfRetries = Integer.parseInt(parameters.get(PlatformKeys.KEY_NO_OF_RETRIES));
    return this;
  }

  public SetRandomisationSettingsRequest build() {
    final SetRandomisationSettingsRequest request = new SetRandomisationSettingsRequest();
    request.setDirectAttach(this.directAttach);
    request.setRandomisationStartWindow(this.randomisationStartWindow);
    request.setMultiplicationFactor(this.multiplicationFactor);
    request.setNumberOfRetries(this.numberOfRetries);
    return request;
  }
}
