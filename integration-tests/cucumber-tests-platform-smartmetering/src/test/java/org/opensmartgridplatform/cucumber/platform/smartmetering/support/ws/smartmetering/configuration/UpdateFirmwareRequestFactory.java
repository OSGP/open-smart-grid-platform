/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class UpdateFirmwareRequestFactory {

  public static UpdateFirmwareRequest fromParameterMap(final Map<String, String> parameters) {
    final UpdateFirmwareRequest updateFirmwareRequest = new UpdateFirmwareRequest();

    updateFirmwareRequest.setDeviceIdentification(
        getString(
            parameters,
            PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
            PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

    updateFirmwareRequest.setFirmwareIdentification(
        parameters.get(PlatformSmartmeteringKeys.FIRMWARE_FILE_IDENTIFICATION));

    return updateFirmwareRequest;
  }

  public static UpdateFirmwareAsyncRequest fromParameterMapAsync(
      final Map<String, String> requestParameters) {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

    final UpdateFirmwareAsyncRequest updateFirmwareAsyncRequest = new UpdateFirmwareAsyncRequest();
    updateFirmwareAsyncRequest.setCorrelationUid(correlationUid);
    updateFirmwareAsyncRequest.setDeviceIdentification(deviceIdentification);
    return updateFirmwareAsyncRequest;
  }

  public static UpdateFirmwareAsyncRequest fromScenarioContext() {
    final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
    final String deviceIdentification =
        RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
    final UpdateFirmwareAsyncRequest request = new UpdateFirmwareAsyncRequest();
    request.setCorrelationUid(correlationUid);
    request.setDeviceIdentification(deviceIdentification);
    return request;
  }
}
