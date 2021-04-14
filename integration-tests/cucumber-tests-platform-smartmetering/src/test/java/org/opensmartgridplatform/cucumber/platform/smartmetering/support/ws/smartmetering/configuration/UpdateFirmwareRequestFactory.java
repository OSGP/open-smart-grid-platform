/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareModuleType;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
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

    updateFirmwareRequest.getFirmwareVersions().addAll(firmwareVersionsFromParameters(parameters));

    return updateFirmwareRequest;
  }

  public static List<FirmwareVersion> firmwareVersionsFromParameters(
      final Map<String, String> parameters) {
    final List<FirmwareVersion> firmwareVersions = new ArrayList<>();
    addFirmwareVersionIfIncluded(
        firmwareVersions,
        FirmwareModuleType.COMMUNICATION_MODULE_ACTIVE_FIRMWARE,
        PlatformKeys.FIRMWARE_MODULE_VERSION_COMM,
        parameters);
    addFirmwareVersionIfIncluded(
        firmwareVersions,
        FirmwareModuleType.MODULE_ACTIVE_FIRMWARE,
        PlatformKeys.FIRMWARE_MODULE_VERSION_MA,
        parameters);
    addFirmwareVersionIfIncluded(
        firmwareVersions,
        FirmwareModuleType.ACTIVE_FIRMWARE,
        PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC,
        parameters);
    return firmwareVersions;
  }

  private static void addFirmwareVersionIfIncluded(
      final List<FirmwareVersion> firmwareVersions,
      final FirmwareModuleType firmwareModuleType,
      final String key,
      final Map<String, String> parameters) {
    if (parameters.containsKey(key)) {
      firmwareVersions.add(firmwareVersion(firmwareModuleType, parameters.get(key)));
    }
  }

  private static FirmwareVersion firmwareVersion(
      final FirmwareModuleType firmwareModuleType, final String version) {
    final FirmwareVersion firmwareVersion = new FirmwareVersion();
    firmwareVersion.setFirmwareModuleType(firmwareModuleType);
    firmwareVersion.setVersion(version);
    return firmwareVersion;
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
