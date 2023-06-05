// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.CoupleMbusDeviceRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class CoupleMbusDeviceRequestFactory {

  private CoupleMbusDeviceRequestFactory() {
    // Private constructor for utility class.
  }

  public static CoupleMbusDeviceRequest forMbusDevice(final String mbusDeviceIdentification) {
    return forGatewayMbusDevice(
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext(),
        mbusDeviceIdentification);
  }

  public static CoupleMbusDeviceRequest forGatewayMbusDevice(
      final String gatewayDeviceIdentification, final String mbusDeviceIdentification) {
    final CoupleMbusDeviceRequest coupleMbusDeviceRequest = new CoupleMbusDeviceRequest();
    coupleMbusDeviceRequest.setDeviceIdentification(gatewayDeviceIdentification);
    coupleMbusDeviceRequest.setMbusDeviceIdentification(mbusDeviceIdentification);
    return coupleMbusDeviceRequest;
  }

  public static CoupleMbusDeviceRequest forGatewayMbusDeviceWithForce(
      final String gatewayDeviceIdentification, final String mbusDeviceIdentification) {

    final CoupleMbusDeviceRequest coupleMbusDeviceRequest =
        forGatewayMbusDevice(gatewayDeviceIdentification, mbusDeviceIdentification);
    coupleMbusDeviceRequest.setForce(true);
    return coupleMbusDeviceRequest;
  }

  public static CoupleMbusDeviceAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final CoupleMbusDeviceAsyncRequest coupleMbusDeviceAsyncRequest =
        new CoupleMbusDeviceAsyncRequest();
    coupleMbusDeviceAsyncRequest.setCorrelationUid(correlationUid);
    coupleMbusDeviceAsyncRequest.setDeviceIdentification(deviceIdentification);
    return coupleMbusDeviceAsyncRequest;
  }
}
