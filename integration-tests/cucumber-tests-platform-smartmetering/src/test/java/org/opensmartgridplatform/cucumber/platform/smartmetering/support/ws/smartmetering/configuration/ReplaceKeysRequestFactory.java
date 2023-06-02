//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetKeysRequestData;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ReplaceKeysRequestFactory {

  private ReplaceKeysRequestFactory() {
    // Private constructor for utility class.
  }

  public static ReplaceKeysRequest fromParameterMap(final Map<String, String> requestParameters) {
    final ReplaceKeysRequest replaceKeysRequest = new ReplaceKeysRequest();
    replaceKeysRequest.setDeviceIdentification(
        getString(
            requestParameters,
            PlatformKeys.KEY_DEVICE_IDENTIFICATION,
            PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION));
    final SetKeysRequestData setKeysRequestData =
        SetKeysRequestDataFactory.fromParameterMap(requestParameters);
    replaceKeysRequest.setSetKeysRequestData(setKeysRequestData);
    return replaceKeysRequest;
  }

  public static ReplaceKeysAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid = requestParameters.get(PlatformKeys.KEY_CORRELATION_UID);
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);
    final ReplaceKeysAsyncRequest replaceKeysAsyncRequest = new ReplaceKeysAsyncRequest();
    replaceKeysAsyncRequest.setCorrelationUid(correlationUid);
    replaceKeysAsyncRequest.setDeviceIdentification(deviceIdentification);
    return replaceKeysAsyncRequest;
  }
}
