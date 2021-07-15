/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import io.cucumber.java.en.Given;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.bundle.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetRandomisationSettingsRequestBuilder;

public class BundledSetRandomisationSettingsSteps extends BaseBundleSteps {
  @Given("the bundle request contains a set randomisation settings action with parameters")
  public void theBundleRequestContainsASetRandomisationSettingsActionWithParameters(
      final Map<String, String> parameters) {
    final SetRandomisationSettingsRequest action =
        new SetRandomisationSettingsRequestBuilder().fromParameterMap(parameters).build();

    this.addActionToBundleRequest(action);
  }
}
