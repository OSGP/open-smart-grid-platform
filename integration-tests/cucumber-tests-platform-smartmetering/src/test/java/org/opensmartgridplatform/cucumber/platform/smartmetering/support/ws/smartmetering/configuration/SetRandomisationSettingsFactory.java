// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetRandomisationSettingsRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetRandomisationSettingsFactory {
  private SetRandomisationSettingsFactory() {}

  public static SetRandomisationSettingsRequest fromParameterMap(
      final Map<String, String> parameters) {
    final SetRandomisationSettingsRequest request = new SetRandomisationSettingsRequest();
    request.setDeviceIdentification(
        parameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    request.setSetRandomisationSettingsData(createRequestData(parameters));
    return request;
  }

  public static SetRandomisationSettingsAsyncRequest fromScenarioContext() {
    final SetRandomisationSettingsAsyncRequest asyncRequest =
        new SetRandomisationSettingsAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }

  private static SetRandomisationSettingsRequestData createRequestData(
      final Map<String, String> parameters) {
    final SetRandomisationSettingsRequestData requestData =
        new SetRandomisationSettingsRequestData();
    requestData.setDirectAttach(Integer.parseInt(parameters.get(PlatformKeys.KEY_DIRECT_ATTACH)));
    requestData.setRandomisationStartWindow(
        Integer.parseInt(parameters.get(PlatformKeys.KEY_RANDOMISATION_START_WINDOW)));
    requestData.setMultiplicationFactor(
        Integer.parseInt(parameters.get(PlatformKeys.KEY_MULTIPLICATION_FACTOR)));
    requestData.setNumberOfRetries(
        Integer.parseInt(parameters.get(PlatformKeys.KEY_NO_OF_RETRIES)));
    return requestData;
  }
}
