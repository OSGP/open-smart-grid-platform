/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
