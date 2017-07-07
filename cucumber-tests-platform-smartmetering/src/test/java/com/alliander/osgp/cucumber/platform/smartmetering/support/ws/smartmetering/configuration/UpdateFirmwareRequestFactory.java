/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.configuration;

import static com.alliander.osgp.cucumber.core.Helpers.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareModuleType;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.FirmwareVersion;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.UpdateFirmwareRequest;
import com.alliander.osgp.cucumber.platform.PlatformKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.RequestFactoryHelper;

public class UpdateFirmwareRequestFactory {

    public static UpdateFirmwareRequest fromParameterMap(final Map<String, String> parameters) {
        final UpdateFirmwareRequest request = new UpdateFirmwareRequest();

        request.setDeviceIdentification(getString(parameters, PlatformSmartmeteringKeys.DEVICE_IDENTIFICATION,
                PlatformSmartmeteringDefaults.DEVICE_IDENTIFICATION));

        request.getFirmwareVersions().addAll(firmwareVersionsFromParameters(parameters));

        return request;
    }

    public static List<FirmwareVersion> firmwareVersionsFromParameters(final Map<String, String> parameters) {
        final List<FirmwareVersion> firmwareVersions = new ArrayList<>();
        addFirmwareVersionIfIncluded(firmwareVersions, FirmwareModuleType.COMMUNICATION_MODULE_ACTIVE_FIRMWARE,
                PlatformKeys.FIRMWARE_MODULE_VERSION_COMM, parameters);
        addFirmwareVersionIfIncluded(firmwareVersions, FirmwareModuleType.MODULE_ACTIVE_FIRMWARE,
                PlatformKeys.FIRMWARE_MODULE_VERSION_MA, parameters);
        addFirmwareVersionIfIncluded(firmwareVersions, FirmwareModuleType.ACTIVE_FIRMWARE,
                PlatformKeys.FIRMWARE_MODULE_VERSION_FUNC, parameters);
        return firmwareVersions;
    }

    private static void addFirmwareVersionIfIncluded(final List<FirmwareVersion> firmwareVersions,
            final FirmwareModuleType firmwareModuleType, final String key, final Map<String, String> parameters) {
        if (parameters.containsKey(key)) {
            firmwareVersions.add(firmwareVersion(firmwareModuleType, parameters.get(key)));
        }
    }

    private static FirmwareVersion firmwareVersion(final FirmwareModuleType firmwareModuleType, final String version) {
        final FirmwareVersion firmwareVersion = new FirmwareVersion();
        firmwareVersion.setFirmwareModuleType(firmwareModuleType);
        firmwareVersion.setVersion(version);
        return firmwareVersion;
    }

    public static UpdateFirmwareAsyncRequest fromParameterMapAsync(final Map<String, String> requestParameters) {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromStepData(requestParameters);

        final UpdateFirmwareAsyncRequest request = new UpdateFirmwareAsyncRequest();
        request.setCorrelationUid(correlationUid);
        request.setDeviceIdentification(deviceIdentification);
        return request;
    }

    public static UpdateFirmwareAsyncRequest fromScenarioContext() {
        final String correlationUid = RequestFactoryHelper.getCorrelationUidFromScenarioContext();
        final String deviceIdentification = RequestFactoryHelper.getDeviceIdentificationFromScenarioContext();
        final UpdateFirmwareAsyncRequest request = new UpdateFirmwareAsyncRequest();
        request.setCorrelationUid(correlationUid);
        request.setDeviceIdentification(deviceIdentification);
        return request;
    }
}
