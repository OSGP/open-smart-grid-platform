// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.math.BigInteger;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupLastGasp;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupLastGaspRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetPushSetupLastGaspRequestFactory {

  private SetPushSetupLastGaspRequestFactory() {
    // Private constructor for utility class
  }

  public static SetPushSetupLastGaspRequest fromParameterMap(final Map<String, String> settings) {
    final SetPushSetupLastGaspRequest request = new SetPushSetupLastGaspRequest();
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    request.setSetPushSetupLastGaspRequestData(createRequestData(settings));
    return request;
  }

  public static SetPushSetupLastGaspAsyncRequest fromScenarioContext() {
    final SetPushSetupLastGaspAsyncRequest asyncRequest = new SetPushSetupLastGaspAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }

  private static SetPushSetupLastGaspRequestData createRequestData(
      final Map<String, String> settings) {

    final PushSetupLastGasp lastGasp = new PushSetupLastGasp();
    lastGasp.setHost(settings.get(PlatformSmartmeteringKeys.HOSTNAME));
    lastGasp.setPort(
        BigInteger.valueOf(Long.valueOf(settings.get(PlatformSmartmeteringKeys.PORT))));

    final SetPushSetupLastGaspRequestData requestData = new SetPushSetupLastGaspRequestData();
    requestData.setPushSetupLastGasp(lastGasp);

    return requestData;
  }
}
