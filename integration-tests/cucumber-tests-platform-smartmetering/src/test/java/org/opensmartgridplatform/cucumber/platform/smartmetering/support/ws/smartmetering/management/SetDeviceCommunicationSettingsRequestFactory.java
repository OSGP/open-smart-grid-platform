//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.management;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.SetDeviceCommunicationSettingsRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class SetDeviceCommunicationSettingsRequestFactory {
  private SetDeviceCommunicationSettingsRequestFactory() {
    // Private constructor for utility class
  }

  public static SetDeviceCommunicationSettingsRequest fromParameterMap(
      final Map<String, String> requestParameters) {
    final SetDeviceCommunicationSettingsRequest setDeviceCommunicationSettingsRequest =
        new SetDeviceCommunicationSettingsRequest();
    setDeviceCommunicationSettingsRequest.setDeviceIdentification(
        requestParameters.get(PlatformSmartmeteringKeys.KEY_DEVICE_IDENTIFICATION));

    setDeviceCommunicationSettingsRequest.setSetDeviceCommunicationSettingsData(
        SetDeviceCommunicationSettingsRequestDataFactory.fromParameterMap(requestParameters));

    return setDeviceCommunicationSettingsRequest;
  }

  public static SetDeviceCommunicationSettingsAsyncRequest fromScenarioContext() {
    final SetDeviceCommunicationSettingsAsyncRequest setDeviceCommunicationSettingsAsyncRequest =
        new SetDeviceCommunicationSettingsAsyncRequest();
    setDeviceCommunicationSettingsAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    setDeviceCommunicationSettingsAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());

    return setDeviceCommunicationSettingsAsyncRequest;
  }
}
