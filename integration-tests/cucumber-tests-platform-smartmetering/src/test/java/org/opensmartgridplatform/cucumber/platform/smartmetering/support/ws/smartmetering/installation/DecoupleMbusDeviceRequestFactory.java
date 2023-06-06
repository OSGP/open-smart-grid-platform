// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DecoupleMbusDeviceRequestFactory {

  private DecoupleMbusDeviceRequestFactory() {
    // Private constructor for utility class.
  }

  public static DecoupleMbusDeviceRequest forGatewayAndMbusDevice(
      final String gatewayDeviceIdentification, final String mbusDeviceIdentification) {
    final DecoupleMbusDeviceRequest decoupleMbusDeviceRequest = new DecoupleMbusDeviceRequest();
    decoupleMbusDeviceRequest.setDeviceIdentification(gatewayDeviceIdentification);
    decoupleMbusDeviceRequest.setMbusDeviceIdentification(mbusDeviceIdentification);
    return decoupleMbusDeviceRequest;
  }

  public static DecoupleMbusDeviceAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final DecoupleMbusDeviceAsyncRequest decoupleMbusDeviceAsyncRequest =
        new DecoupleMbusDeviceAsyncRequest();
    decoupleMbusDeviceAsyncRequest.setCorrelationUid(correlationUid);
    decoupleMbusDeviceAsyncRequest.setDeviceIdentification(deviceIdentification);
    return decoupleMbusDeviceAsyncRequest;
  }
}
