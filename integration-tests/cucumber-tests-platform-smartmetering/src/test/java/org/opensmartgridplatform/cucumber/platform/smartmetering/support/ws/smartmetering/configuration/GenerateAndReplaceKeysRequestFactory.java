// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.GenerateAndReplaceKeysRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class GenerateAndReplaceKeysRequestFactory {

  private GenerateAndReplaceKeysRequestFactory() {
    // Private constructor for utility class.
  }

  public static GenerateAndReplaceKeysRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final GenerateAndReplaceKeysRequest generateAndReplaceKeysRequest =
        new GenerateAndReplaceKeysRequest();
    generateAndReplaceKeysRequest.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    return generateAndReplaceKeysRequest;
  }

  public static GenerateAndReplaceKeysAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid = requestParameters.get(PlatformKeys.KEY_CORRELATION_UID);
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);
    final GenerateAndReplaceKeysAsyncRequest generateAndReplaceKeysAsyncRequest =
        new GenerateAndReplaceKeysAsyncRequest();
    generateAndReplaceKeysAsyncRequest.setCorrelationUid(correlationUid);
    generateAndReplaceKeysAsyncRequest.setDeviceIdentification(deviceIdentification);
    return generateAndReplaceKeysAsyncRequest;
  }
}
