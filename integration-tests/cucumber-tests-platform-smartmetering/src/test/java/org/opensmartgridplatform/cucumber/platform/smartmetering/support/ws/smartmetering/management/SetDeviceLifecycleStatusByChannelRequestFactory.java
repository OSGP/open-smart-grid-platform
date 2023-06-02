//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.DeviceLifecycleStatus;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceLifecycleStatusByChannelRequestData;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetDeviceLifecycleStatusByChannelRequestFactory {

  private SetDeviceLifecycleStatusByChannelRequestFactory() {
    // Private constructor for utility class
  }

  public static SetDeviceLifecycleStatusByChannelRequest fromParameterMap(
      final Map<String, String> settings) {
    final SetDeviceLifecycleStatusByChannelRequest request =
        new SetDeviceLifecycleStatusByChannelRequest();
    final SetDeviceLifecycleStatusByChannelRequestData requestData =
        new SetDeviceLifecycleStatusByChannelRequestData();
    request.setGatewayDeviceIdentification(
        settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));
    requestData.setChannel(Short.parseShort(settings.get(PlatformSmartmeteringKeys.KEY_CHANNEL)));
    requestData.setDeviceLifecycleStatus(
        DeviceLifecycleStatus.valueOf(
            settings.get(PlatformSmartmeteringKeys.KEY_DEVICE_LIFECYCLE_STATUS)));
    request.setSetDeviceLifecycleStatusByChannelRequestData(requestData);
    return request;
  }

  public static SetDeviceLifecycleStatusByChannelAsyncRequest fromScenarioContext() {
    final SetDeviceLifecycleStatusByChannelAsyncRequest asyncRequest =
        new SetDeviceLifecycleStatusByChannelAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

    return asyncRequest;
  }
}
