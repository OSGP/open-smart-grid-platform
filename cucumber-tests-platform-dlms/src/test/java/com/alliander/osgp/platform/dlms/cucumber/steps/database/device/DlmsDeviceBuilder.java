/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import static com.alliander.osgp.platform.cucumber.core.Helpers.getBoolean;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getDate;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getLong;
import static com.alliander.osgp.platform.cucumber.core.Helpers.getString;

import java.util.Date;
import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.repositories.DlmsSecurityKeyRepository;
import org.springframework.beans.factory.annotation.Autowired;

import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class DlmsDeviceBuilder implements DeviceBuilder {

    @Autowired
    private DlmsSecurityKeyRepository securityKeyRepository;

    private final Map<String, String> inputSettings;

    private String deviceId = null;
    private Long version = Defaults.DEFAULT_VERSION;
    private String iccId = null;
    private String communicationProvider = null;
    private String communicationMethod = null;
    private boolean hls3active;
    private boolean hls4active;
    private boolean hls5active;
    private Long challengeLength = null;
    private boolean withListSupported;
    private boolean selectiveAccessSupported;
    private boolean ipAddressIsStatic;
    private Long port;
    private Long clientId = null;
    private Long logicalId;
    private boolean inDebugMode;

    private String organisationId = "test-org";

    private String dlmsDeviceId = null;
    private String securityKeyType = null;
    private Date validFrom = null;
    private Date validTo = null;
    private String securityKey = null;

    DlmsDeviceBuilder(final Map<String, String> inputSettings) {
        // Remove the parameter and class instance of inputSettings.
        this.inputSettings = inputSettings;
    }

    @Override
    public DlmsDeviceBuilder setDeviceId() {
        this.deviceId = getString(this.inputSettings, Keys.KEY_DEVICE_IDENTIFICATION,
                Defaults.DEFAULT_DEVICE_IDENTIFICATION);
        return this;
    }

    public DeviceBuilder setVersion(final Long version) {
        // Pass a parameter to each setter like any normal builder pattern does.
        this.version = version;
        return this;
    }

    public DeviceBuilder setIccId() {
        this.iccId = getString(this.inputSettings, Keys.KEY_ICC_ID, Defaults.DEFAULT_ICC_ID);
        return this;
    }

    public DeviceBuilder setCommunicationProvider() {
        this.communicationProvider = getString(this.inputSettings, Keys.KEY_COMMUNICATION_PROVIDER,
                Defaults.DEFAULT_COMMUNICATION_PROVIDER);
        return this;
    }

    public DeviceBuilder setCommunicationMethod() {
        this.communicationMethod = getString(this.inputSettings, Keys.KEY_COMMUNICATION_METHOD,
                Defaults.DEFAULT_COMMUNICATION_METHOD);
        return this;
    }

    public DeviceBuilder setHls3active() {
        this.hls3active = getBoolean(this.inputSettings, Keys.KEY_HLS3ACTIVE, Defaults.DEFAULT_HLS3ACTIVE);
        return this;
    }

    public DeviceBuilder setHls4active() {
        this.hls4active = getBoolean(this.inputSettings, Keys.KEY_HLS4ACTIVE, Defaults.DEFAULT_HLS4ACTIVE);
        return this;
    }

    public DeviceBuilder setHls5active() {
        this.hls5active = getBoolean(this.inputSettings, Keys.KEY_HLS5ACTIVE, Defaults.DEFAULT_HLS5ACTIVE);
        return this;
    }

    public DeviceBuilder setChallengeLength() {
        this.challengeLength = getLong(this.inputSettings, Keys.KEY_CHALLENGE_LENGTH, Defaults.DEFAULT_CHALLENGE_LENGTH);
        return this;
    }

    public DeviceBuilder setWithListSupported() {
        this.withListSupported = getBoolean(this.inputSettings, Keys.KEY_WITH_LIST_SUPPORTED,
                Defaults.DEFAULT_WITH_LIST_SUPPORTED);
        return this;
    }

    public DeviceBuilder setSelectiveAccessSupported() {
        this.selectiveAccessSupported = getBoolean(this.inputSettings, Keys.KEY_SELECTIVE_ACCESS_SUPPORTED,
                Defaults.DEFAULT_SELECTIVE_ACCESS_SUPPORTED);
        return this;
    }

    public DeviceBuilder setIpAddressIsStatic() {
        this.ipAddressIsStatic = getBoolean(this.inputSettings, Keys.KEY_IP_ADDRESS_IS_STATIC,
                Defaults.DEFAULT_IP_ADDRESS_IS_STATIC);
        return this;
    }

    public DeviceBuilder setPort() {
        this.port = getLong(this.inputSettings, Keys.KEY_PORT, Defaults.DEFAULT_PORT);
        return this;
    }

    public DeviceBuilder setClientId() {
        this.clientId = getLong(this.inputSettings, Keys.KEY_CLIENT_ID, Defaults.DEFAULT_CLIENT_ID);
        return this;
    }

    public DeviceBuilder setLogicalId() {
        this.logicalId = getLong(this.inputSettings, Keys.KEY_LOGICAL_ID, Defaults.DEFAULT_LOGICAL_ID);
        return this;
    }

    public DeviceBuilder setInDebugMode() {
        this.inDebugMode = getBoolean(this.inputSettings, Keys.KEY_IN_DEBUG_MODE, Defaults.DEFAULT_IN_DEBUG_MODE);
        return this;
    }

    public DeviceBuilder setOrganisationId() {
        this.organisationId = getString(this.inputSettings, "OrganisationId", this.organisationId);
        return this;
    }

    public DeviceBuilder setDlmsDeviceId() {
        this.dlmsDeviceId = this.inputSettings.get(Keys.KEY_DLMS_DEVICE_ID);
        return this;
    }

    public DeviceBuilder setSecurityKeyType() {
        this.securityKeyType = getString(this.inputSettings, Keys.KEY_SECURITY_KEY_TYPE_A, this.securityKeyType);
        return this;
    }

    public DeviceBuilder setValidFrom() {
        this.validFrom = getDate(this.inputSettings, Keys.KEY_VALID_FROM).toDate();
        return this;
    }

    public DeviceBuilder setValidTo() {
        this.validTo = getDate(this.inputSettings, Keys.KEY_VALID_TO).toDate();
        return this;
    }

    public DeviceBuilder setSecurityKey() {
        this.securityKey = getString(this.inputSettings, Keys.KEY_SECURITY_KEY_A, this.securityKey);
        return this;
    }

    public DlmsDevice buildDlmsDevice(final Map<String, String> inputSettings) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(this.deviceId);

        // Put the inputSettings parsing logic in only this one method.
        if (inputSettings.containsKey(Keys.KEY_VERSION)) {
            // Type casting can be done directly.
            dlmsDevice.setVersion(Long.parseLong(inputSettings.get(Keys.KEY_VERSION)));
        }

        dlmsDevice.setIccId(this.iccId);
        dlmsDevice.setCommunicationProvider(this.communicationProvider);
        dlmsDevice.setCommunicationMethod(this.communicationMethod);
        dlmsDevice.setHls3Active(this.hls3active);
        dlmsDevice.setHls4Active(this.hls4active);
        dlmsDevice.setHls5Active(this.hls5active);
        dlmsDevice.setChallengeLength(this.challengeLength.intValue()); // getInt??
        dlmsDevice.setWithListSupported(this.withListSupported);
        dlmsDevice.setSelectiveAccessSupported(this.selectiveAccessSupported);
        dlmsDevice.setIpAddressIsStatic(this.ipAddressIsStatic);
        dlmsDevice.setPort(this.port);
        dlmsDevice.setClientId(this.clientId);
        dlmsDevice.setLogicalId(this.logicalId);
        dlmsDevice.setInDebugMode(this.inDebugMode);
        this.buildSecurityKey();
        return dlmsDevice;
    }

    public void buildSecurityKey() {
        final SecurityKey securityKey = new SecurityKey();
        final DlmsDevice dlmsDevice = new DlmsDevice();
        dlmsDevice.setDeviceIdentification(this.deviceId);
        securityKey.setVersion(this.version);
        securityKey.setValidFrom(this.validFrom);
        securityKey.setValidTo(this.validTo);
        securityKey.getSecurityKeyType();

        // this.securityKeyRepository.save();
    }
}
