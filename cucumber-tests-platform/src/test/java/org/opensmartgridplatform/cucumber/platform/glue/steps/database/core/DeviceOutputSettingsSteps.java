/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getEnum;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.opensmartgridplatform.cucumber.core.GlueBase;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceOutputSetting;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.RelayStatusRepository;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.opensmartgridplatform.domain.core.valueobjects.RelayType;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;

public class DeviceOutputSettingsSteps extends GlueBase {

    @Autowired
    private SsldRepository ssldRepository;

    @Autowired
    private RelayStatusRepository relayStatusRepository;

    @Given("^a device output setting$")
    public void aDeviceOutputSetting(final Map<String, String> settings) {

        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        final Ssld device = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

        final List<DeviceOutputSetting> outputSettings = new ArrayList<>();
        final DeviceOutputSetting deviceOutputSetting = new DeviceOutputSetting(
                getInteger(settings, PlatformKeys.KEY_INTERNALID,
                        PlatformDefaults.DEFAULT_DEVICE_OUTPUT_SETTING_INTERNALID),
                getInteger(settings, PlatformKeys.KEY_EXTERNALID,
                        PlatformDefaults.DEFAULT_DEVICE_OUTPUT_SETTING_EXTERNALID),
                getEnum(settings, PlatformKeys.KEY_RELAY_TYPE, RelayType.class,
                        PlatformDefaults.DEFAULT_DEVICE_OUTPUT_SETTING_RELAY_TYPE),
                getString(settings, PlatformKeys.KEY_ALIAS, PlatformDefaults.DEFAULT_DEVICE_OUTPUT_SETTING_ALIAS));
        outputSettings.add(deviceOutputSetting);

        this.saveDeviceOutputSettingsAndRelayStatuses(outputSettings, device);
    }

    @Given("^device output settings$")
    public void deviceOutputSettings(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        final Ssld device = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

        final String[] deviceOutputSettings = getString(settings, PlatformKeys.DEVICE_OUTPUT_SETTINGS, "")
                .split(PlatformKeys.SEPARATOR_SEMICOLON);

        final List<DeviceOutputSetting> outputSettings = new ArrayList<>();
        for (final String deviceOutputSetting : deviceOutputSettings) {
            final String[] deviceOutputSettingsPart = deviceOutputSetting.split(PlatformKeys.SEPARATOR_COMMA);

            final DeviceOutputSetting deviceOutputSettingsForLightValue = new DeviceOutputSetting(
                    Integer.parseInt(deviceOutputSettingsPart[0]), Integer.parseInt(deviceOutputSettingsPart[1]),
                    Enum.valueOf(RelayType.class, deviceOutputSettingsPart[2]), deviceOutputSettingsPart[3]);
            outputSettings.add(deviceOutputSettingsForLightValue);
        }

        this.saveDeviceOutputSettingsAndRelayStatuses(outputSettings, device);
    }

    @Given("^device output settings for lightvalues$")
    public void deviceOutputSettingsForLightValues(final Map<String, String> settings) {
        final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION);

        final Ssld device = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

        final String[] lightValues = getString(settings, PlatformKeys.KEY_LIGHTVALUES,
                PlatformDefaults.DEFAULT_DEVICE_IDENTIFICATION).split(PlatformKeys.SEPARATOR_SEMICOLON);

        final String[] deviceOutputSettings = getString(settings, PlatformKeys.DEVICE_OUTPUT_SETTINGS, "")
                .split(PlatformKeys.SEPARATOR_SEMICOLON);

        final List<DeviceOutputSetting> outputSettings = new ArrayList<>();
        for (int i = 0; i < lightValues.length; i++) {

            final String[] lightValueParts = lightValues[i].split(PlatformKeys.SEPARATOR_COMMA);

            final String[] deviceOutputSettingsPart = deviceOutputSettings[i].split(PlatformKeys.SEPARATOR_COMMA);

            final DeviceOutputSetting deviceOutputSettingsForLightValue = new DeviceOutputSetting(
                    Integer.parseInt(deviceOutputSettingsPart[0]), Integer.parseInt(lightValueParts[0]),
                    Enum.valueOf(RelayType.class, deviceOutputSettingsPart[1]), deviceOutputSettingsPart[2]);
            outputSettings.add(deviceOutputSettingsForLightValue);
        }

        this.saveDeviceOutputSettingsAndRelayStatuses(outputSettings, device);
    }

    private void saveDeviceOutputSettingsAndRelayStatuses(final List<DeviceOutputSetting> deviceOutputSettings,
            final Ssld device) {
        device.updateOutputSettings(deviceOutputSettings);
        this.ssldRepository.save(device);

        // Create a dummy relay status for each device output setting
        for (final DeviceOutputSetting deviceOutputSetting : deviceOutputSettings) {
            final RelayStatus relayStatus = new RelayStatus.Builder(device, deviceOutputSetting.getExternalId())
                    .withLastSwitchingEventState(false, new Date()).build();

            this.relayStatusRepository.save(relayStatus);
        }
    }
}
