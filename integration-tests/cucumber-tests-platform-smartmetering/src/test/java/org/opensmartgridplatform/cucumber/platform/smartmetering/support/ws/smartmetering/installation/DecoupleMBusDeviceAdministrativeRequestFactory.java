// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.installation;

import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMBusDeviceAdministrativeRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.installation.DecoupleMbusDeviceAdministrativeAsyncRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class DecoupleMBusDeviceAdministrativeRequestFactory {

  private DecoupleMBusDeviceAdministrativeRequestFactory() {
    // Private constructor for utility class.
  }

  public static DecoupleMBusDeviceAdministrativeRequest forMbusDevice(
      final String mbusDeviceIdentification) {
    final DecoupleMBusDeviceAdministrativeRequest decoupleMbusDeviceRequest =
        new DecoupleMBusDeviceAdministrativeRequest();
    decoupleMbusDeviceRequest.setMbusDeviceIdentification(mbusDeviceIdentification);
    return decoupleMbusDeviceRequest;
  }

  public static DecoupleMbusDeviceAdministrativeAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final DecoupleMbusDeviceAdministrativeAsyncRequest
        decoupleMbusDeviceAdministrativeAsyncRequest =
            new DecoupleMbusDeviceAdministrativeAsyncRequest();
    decoupleMbusDeviceAdministrativeAsyncRequest.setCorrelationUid(correlationUid);
    decoupleMbusDeviceAdministrativeAsyncRequest.setDeviceIdentification(deviceIdentification);
    return decoupleMbusDeviceAdministrativeAsyncRequest;
  }
}
