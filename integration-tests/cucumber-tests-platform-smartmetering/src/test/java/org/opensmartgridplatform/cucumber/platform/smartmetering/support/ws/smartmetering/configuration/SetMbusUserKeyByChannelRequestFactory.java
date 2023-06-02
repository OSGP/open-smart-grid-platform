//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.SetMbusUserKeyByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetMbusUserKeyByChannelRequestFactory {
  private SetMbusUserKeyByChannelRequestFactory() {
    // Private constructor for utility class
  }

  public static SetMbusUserKeyByChannelRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetMbusUserKeyByChannelRequest setMbusUserKeyByChannelRequest =
        new SetMbusUserKeyByChannelRequest();
    setMbusUserKeyByChannelRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    final SetMbusUserKeyByChannelRequestData setMbusUserKeyByChannelRequestData =
        SetMbusUserKeyByChannelRequestDataFactory.fromParameterMap(requestParameters);
    setMbusUserKeyByChannelRequest.setSetMbusUserKeyByChannelRequestData(
        setMbusUserKeyByChannelRequestData);
    return setMbusUserKeyByChannelRequest;
  }

  public static SetMbusUserKeyByChannelAsyncRequest fromScenarioContext() {
    final SetMbusUserKeyByChannelAsyncRequest setMbusUserKeyByChannelAsyncRequest =
        new SetMbusUserKeyByChannelAsyncRequest();
    setMbusUserKeyByChannelAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setMbusUserKeyByChannelAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return setMbusUserKeyByChannelAsyncRequest;
  }
}
