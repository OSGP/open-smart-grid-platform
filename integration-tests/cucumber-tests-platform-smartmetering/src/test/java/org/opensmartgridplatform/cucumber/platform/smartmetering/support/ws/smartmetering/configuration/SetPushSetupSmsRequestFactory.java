// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.math.BigInteger;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.PushSetupSms;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetPushSetupSmsRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetPushSetupSmsRequestFactory {

  private SetPushSetupSmsRequestFactory() {
    // Private constructor for utility class
  }

  public static SetPushSetupSmsRequest fromParameterMap(final Map<String, String> settings) {
    final SetPushSetupSmsRequest request = new SetPushSetupSmsRequest();
    request.setDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    request.setSetPushSetupSmsRequestData(createRequestData(settings));
    return request;
  }

  public static SetPushSetupSmsAsyncRequest fromScenarioContext() {
    final SetPushSetupSmsAsyncRequest asyncRequest = new SetPushSetupSmsAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }

  private static SetPushSetupSmsRequestData createRequestData(final Map<String, String> settings) {

    final PushSetupSms sms = new PushSetupSms();
    sms.setHost(settings.get(PlatformSmartmeteringKeys.HOSTNAME));
    sms.setPort(BigInteger.valueOf(Long.valueOf(settings.get(PlatformSmartmeteringKeys.PORT))));

    final SetPushSetupSmsRequestData requestData = new SetPushSetupSmsRequestData();
    requestData.setPushSetupSms(sms);

    return requestData;
  }
}
