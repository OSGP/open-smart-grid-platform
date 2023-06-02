//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.adhoc;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.adhoc.ScanMbusChannelsRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class ScanMbusChannelsRequestFactory {

  private ScanMbusChannelsRequestFactory() {
    // Private constructor for utility class
  }

  public static ScanMbusChannelsRequest fromParameterMap(final Map<String, String> parameters) {
    final ScanMbusChannelsRequest request = new ScanMbusChannelsRequest();
    request.setDeviceIdentification(
        parameters.get(PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION));
    return request;
  }

  public static ScanMbusChannelsAsyncRequest fromScenarioContext() {
    final ScanMbusChannelsAsyncRequest asyncRequest = new ScanMbusChannelsAsyncRequest();
    asyncRequest.setCorrelationUid(RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    asyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return asyncRequest;
  }
}
