//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
