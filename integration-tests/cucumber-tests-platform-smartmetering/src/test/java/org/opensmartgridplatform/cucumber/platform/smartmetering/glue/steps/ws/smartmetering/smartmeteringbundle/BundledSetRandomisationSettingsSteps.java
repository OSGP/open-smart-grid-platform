// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
