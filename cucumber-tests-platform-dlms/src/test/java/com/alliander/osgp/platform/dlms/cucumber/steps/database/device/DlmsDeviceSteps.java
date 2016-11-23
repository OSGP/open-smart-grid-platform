/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import static com.alliander.osgp.platform.cucumber.steps.Defaults.SMART_METER_E;
import static com.alliander.osgp.platform.cucumber.steps.Defaults.SMART_METER_G;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.domain.core.entities.Device;
import com.alliander.osgp.domain.core.entities.ProtocolInfo;
import com.alliander.osgp.domain.core.entities.SmartMeter;
import com.alliander.osgp.domain.core.repositories.DeviceRepository;
import com.alliander.osgp.domain.core.repositories.ProtocolInfoRepository;
import com.alliander.osgp.domain.core.repositories.SmartMeterRepository;
import com.alliander.osgp.platform.cucumber.core.ScenarioContext;
import com.alliander.osgp.platform.dlms.cucumber.builders.entities.DeviceBuilder;
import com.alliander.osgp.platform.dlms.cucumber.builders.entities.DlmsDeviceBuilder;
import com.alliander.osgp.platform.dlms.cucumber.builders.entities.SmartMeterBuilder;
import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

import cucumber.api.java.en.Given;

/**
 * DLMS device specific steps.
 */
public class DlmsDeviceSteps {

    @Autowired
    private SmartMeterRepository smartMeterRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private DlmsDeviceRepository dlmsDeviceRepository;

    @Autowired
    private ProtocolInfoRepository protocolInfoRepository;

    @Given("^a dlms device$")
    public void aDlmsDevice(final Map<String, String> inputSettings) throws Throwable {
        if (this.isSmartMeter(inputSettings)) {
            final SmartMeter smartMeter = new SmartMeterBuilder().withSettings(inputSettings)
                    .setProtocolInfo(this.getProtocolInfo(inputSettings)).build();
            this.smartMeterRepository.save(smartMeter);

            ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, smartMeter.getDeviceIdentification());
        } else {
            final Device device = new DeviceBuilder().withSettings(inputSettings)
                    .setProtocolInfo(this.getProtocolInfo(inputSettings)).build();
            this.deviceRepository.save(device);

            ScenarioContext.Current().put(Keys.KEY_DEVICE_IDENTIFICATION, device.getDeviceIdentification());
        }

        // Protocol adapter
        final DlmsDevice dlmsDevice = new DlmsDeviceBuilder().withSettings(inputSettings).build();
        this.dlmsDeviceRepository.save(dlmsDevice);
    }

    private boolean isSmartMeter(final Map<String, String> settings) {
        final String deviceType = settings.get(Keys.KEY_DEVICE_TYPE);
        return SMART_METER_E.equals(deviceType) || SMART_METER_G.equals(deviceType);
    }

    /**
     * ProtocolInfo is fixed system data, inserted by flyway. Therefore the
     * ProtocolInfo instance will be retrieved from the database, and not built.
     *
     * @param inputSettings
     * @return ProtocolInfo
     */
    private ProtocolInfo getProtocolInfo(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(Keys.KEY_PROTOCOL) && inputSettings.containsKey(Keys.KEY_PROTOCOL_VERSION)) {
            return this.protocolInfoRepository.findByProtocolAndProtocolVersion(inputSettings.get(Keys.KEY_PROTOCOL),
                    inputSettings.get(Keys.KEY_PROTOCOL_VERSION));
        } else {
            return this.protocolInfoRepository.findByProtocolAndProtocolVersion(Defaults.DEFAULT_PROTOCOL,
                    Defaults.DEFAULT_PROTOCOL_VERSION);
        }
    }
}
