// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class AddDeviceRequestFactory {

  private AddDeviceRequestFactory() {
    // Private constructor for utility class.
  }

  public static AddDeviceRequest fromParameterMap(final Map<String, String> requestParameters) {
    final AddDeviceRequest addDeviceRequest = new AddDeviceRequest();
    addDeviceRequest.setDevice(DeviceFactory.fromParameterMap(requestParameters));
    addDeviceRequest.setDeviceModel(DeviceModelFactory.fromParameterMap(requestParameters));
    return addDeviceRequest;
  }

  public static AddDeviceAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);
    final AddDeviceAsyncRequest addDeviceAsyncRequest = new AddDeviceAsyncRequest();
    addDeviceAsyncRequest.setCorrelationUid(correlationUid);
    addDeviceAsyncRequest.setDeviceIdentification(deviceIdentification);
    return addDeviceAsyncRequest;
  }

  public static AddDeviceAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final AddDeviceAsyncRequest addDeviceAsyncRequest = new AddDeviceAsyncRequest();
    addDeviceAsyncRequest.setCorrelationUid(correlationUid);
    addDeviceAsyncRequest.setDeviceIdentification(deviceIdentification);
    return addDeviceAsyncRequest;
  }
}
