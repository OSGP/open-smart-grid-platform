// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.monitoring;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.monitoring.PeriodicMeterReadsRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class PeriodicMeterReadsRequestFactory {
  public static PeriodicMeterReadsRequest fromParameterMap(
      final Map<String, String> requestParameters) {

    final PeriodicMeterReadsRequest periodicMeterReadsRequest = new PeriodicMeterReadsRequest();

    periodicMeterReadsRequest.setDeviceIdentification(
        requestParameters.get(PlatformKeys.KEY_DEVICE_IDENTIFICATION));
    periodicMeterReadsRequest.setPeriodicReadsRequestData(
        PeriodicReadsRequestDataFactory.fromParameterMap(requestParameters));

    return periodicMeterReadsRequest;
  }

  public static PeriodicMeterReadsAsyncRequest fromScenarioContext() {
    final PeriodicMeterReadsAsyncRequest periodicMeterReadsAsyncRequest =
        new PeriodicMeterReadsAsyncRequest();
    periodicMeterReadsAsyncRequest.setCorrelationUid(
        RequestFactoryHelper.getCorrelationUidFromScenarioContext());
    periodicMeterReadsAsyncRequest.setDeviceIdentification(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext());
    return periodicMeterReadsAsyncRequest;
  }
}
