/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.steps.database.core;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getEnum;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getInteger;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alliander.osgp.domain.core.entities.DeviceOutputSetting;
import com.alliander.osgp.domain.core.entities.Ssld;
import com.alliander.osgp.domain.core.repositories.SsldRepository;
import com.alliander.osgp.domain.core.valueobjects.RelayType;
import com.alliander.osgp.platform.cucumber.steps.Defaults;
import com.alliander.osgp.platform.cucumber.steps.Keys;

import cucumber.api.java.en.Given;

@Transactional("txMgrCore")
public class DeviceOutputSettingsSteps {

    @Autowired
    private SsldRepository ssldRepository;

    /**
     * 
     * @param settings
     * @throws Throwable
     */
    @Given("^a device output setting$")
    public void aDeviceOutputSetting(final Map<String, String> settings) throws Throwable {
        
        String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        
        Ssld device = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
        
        List<DeviceOutputSetting> outputSettings = new ArrayList<DeviceOutputSetting>();
        DeviceOutputSetting deviceOutputSetting = new DeviceOutputSetting(
                getInteger(settings, Keys.KEY_INTERNALID, Defaults.DEVICE_OUTPUT_SETTING_INTERNALID), 
                getInteger(settings, Keys.KEY_EXTERNALID, Defaults.DEVICE_OUTPUT_SETTING_EXTERNALID), 
                getEnum(settings, Keys.KEY_RELAY_TYPE, RelayType.class, Defaults.DEVICE_OUTPUT_SETTING_RELAY_TYPE),
                getString(settings, Keys.KEY_ALIAS, Defaults.DEVICE_OUTPUT_SETTING_ALIAS));
        outputSettings.add(deviceOutputSetting);
        device.updateOutputSettings(outputSettings);
        
        this.ssldRepository.save(device);
    }
    
    @Given("^device output settings for lightvalues$")
    public void deviceOutputSettingsForLightValues(final Map<String, String> settings) throws Throwable {
        String deviceIdentification = getString(settings, Keys.KEY_DEVICE_IDENTIFICATION, Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        
        Ssld device = this.ssldRepository.findByDeviceIdentification(deviceIdentification);
        
        String[] lightValues = getString(settings, Keys.KEY_LIGHTVALUES, Defaults.DEFAULT_DEVICE_IDENTIFICATION).split(Keys.SEPARATOR);
        
        String[] deviceOutputSettings = getString(settings, Keys.DEVICE_OUTPUT_SETTINGS, "").split(Keys.SEPARATOR);
        
        List<DeviceOutputSetting> outputSettings = new ArrayList<>();
        for (int i = 0; i < lightValues.length;i++) {
            
            final String[] lightValueParts = lightValues[i].split(Keys.SEPARATOR_SEMICOLON); 
            
            final String[] deviceOutputSettingsPart = deviceOutputSettings[i].split(Keys.SEPARATOR_SEMICOLON);
            
            DeviceOutputSetting deviceOutputSettingsForLightValue = new DeviceOutputSetting(
                    Integer.parseInt(deviceOutputSettingsPart[0]), 
                    Integer.parseInt(lightValueParts[0]), 
                    Enum.valueOf(RelayType.class, deviceOutputSettingsPart[1]),
                    deviceOutputSettingsPart[2]);
            outputSettings.add(deviceOutputSettingsForLightValue);
        }

        device.updateOutputSettings(outputSettings);
        
        this.ssldRepository.save(device);
    }
}
