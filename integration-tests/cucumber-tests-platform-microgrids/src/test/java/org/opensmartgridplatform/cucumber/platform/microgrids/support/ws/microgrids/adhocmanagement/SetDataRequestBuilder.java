// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.microgrids.support.ws.microgrids.adhocmanagement;

import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataRequest;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.adhocmanagement.SetDataSystemIdentifier;
import org.opensmartgridplatform.adapter.ws.schema.microgrids.common.AsyncRequest;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;

public class SetDataRequestBuilder {

  private SetDataRequestBuilder() {
    // Private constructor for utility class.
  }

  public static SetDataRequest fromParameterMap(final Map<String, String> requestParameters) {
    final SetDataRequest setDataRequest = new SetDataRequest();
    setDataRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));

    final List<SetDataSystemIdentifier> systems =
        new SetDataSystemIdentifierBuilder().withSettings(requestParameters).buildList();
    setDataRequest.getSystem().addAll(systems);

    return setDataRequest;
  }

  public static SetDataAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid =
        (String) ScenarioContext.current().get(PlatformKeys.KEY_CORRELATION_UID);
    if (correlationUid == null) {
      throw new AssertionError(
          "ScenarioContext must contain the correlation UID for key \""
              + PlatformKeys.KEY_CORRELATION_UID
              + "\" before creating an async request.");
    }
    final String deviceIdentification =
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    if (deviceIdentification == null) {
      throw new AssertionError(
          "The Step DataTable must contain the device identification for key \""
              + PlatformKeys.KEY_DEVICE_IDENTIFICATION
              + "\" when creating an async request.");
    }
    final SetDataAsyncRequest setDataAsyncRequest = new SetDataAsyncRequest();
    final AsyncRequest asyncRequest = new AsyncRequest();
    asyncRequest.setCorrelationUid(correlationUid);
    asyncRequest.setDeviceId(deviceIdentification);
    setDataAsyncRequest.setAsyncRequest(asyncRequest);
    return setDataAsyncRequest;
  }
}
