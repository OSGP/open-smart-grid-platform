/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.steps.database.device;

import java.util.Map;

import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;

import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class DlmsDeviceBuilder {

    private String deviceId = Defaults.DEFAULT_DEVICE_IDENTIFICATION;
    private Long version = Defaults.DEFAULT_VERSION;
    private String iccId = Defaults.DEFAULT_ICC_ID;
    private String communicationProvider = Defaults.DEFAULT_COMMUNICATION_PROVIDER;
    private String communicationMethod = Defaults.DEFAULT_COMMUNICATION_METHOD;
    private boolean hls3active = Defaults.DEFAULT_HLS3ACTIVE;
    private boolean hls4active = Defaults.DEFAULT_HLS4ACTIVE;
    private boolean hls5active = Defaults.DEFAULT_HLS5ACTIVE;
    private Long challengeLength = Defaults.DEFAULT_CHALLENGE_LENGTH;
    private boolean withListSupported = Defaults.DEFAULT_WITH_LIST_SUPPORTED;
    private boolean selectiveAccessSupported = Defaults.DEFAULT_SELECTIVE_ACCESS_SUPPORTED;
    private boolean ipAddressIsStatic = Defaults.DEFAULT_IP_ADDRESS_IS_STATIC;
    private Long port = Defaults.DEFAULT_PORT;
    private Long clientId = Defaults.DEFAULT_CLIENT_ID;
    private Long logicalId = Defaults.DEFAULT_LOGICAL_ID;
    private boolean inDebugMode = Defaults.DEFAULT_IN_DEBUG_MODE;

    private String organisationId = "test-org";
    private SecurityKeyBuilder securityKeyBuilder;

    public DlmsDeviceBuilder setDeviceId(final String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public DlmsDeviceBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }

    public DlmsDeviceBuilder setIccId(final String iccId) {
        this.iccId = iccId;
        return this;
    }

    public DlmsDeviceBuilder setCommunicationProvider(final String communicationProvider) {
        this.communicationProvider = communicationProvider;
        return this;
    }

    public DlmsDeviceBuilder setCommunicationMethod(final String communicationMethod) {
        this.communicationMethod = communicationMethod;
        return this;
    }

    public DlmsDeviceBuilder setHls3active(final boolean hls3active) {
        this.hls3active = hls3active;
        return this;
    }

    public DlmsDeviceBuilder setHls4active(final boolean hls4active) {
        this.hls4active = hls4active;
        return this;
    }

    public DlmsDeviceBuilder setHls5active(final boolean hls5active) {
        this.hls5active = hls5active;
        return this;
    }

    public DlmsDeviceBuilder setChallengeLength(final Long challengeLength) {
        this.challengeLength = challengeLength;
        return this;
    }

    public DlmsDeviceBuilder setWithListSupported(final boolean withListSupported) {
        this.withListSupported = withListSupported;
        return this;
    }

    public DlmsDeviceBuilder setSelectiveAccessSupported(final boolean selectiveAccessSupported) {
        this.selectiveAccessSupported = selectiveAccessSupported;
        return this;
    }

    public DlmsDeviceBuilder setIpAddressIsStatic(final boolean ipAddressIsStatic) {
        this.ipAddressIsStatic = ipAddressIsStatic;
        return this;
    }

    public DlmsDeviceBuilder setPort(final Long port) {
        this.port = port;
        return this;
    }

    public DlmsDeviceBuilder setClientId(final Long clientId) {
        this.clientId = clientId;
        return this;
    }

    public DlmsDeviceBuilder setLogicalId(final Long logicalId) {
        this.logicalId = logicalId;
        return this;
    }

    public DlmsDeviceBuilder setInDebugMode(final boolean inDebugMode) {
        this.inDebugMode = inDebugMode;
        return this;
    }

    public DlmsDeviceBuilder setOrganisationId(final String organisationId) {
        this.organisationId = organisationId;
        return this;
    }

    public DlmsDeviceBuilder setSecurityKey(final SecurityKeyBuilder securityKeyBuilder) {
        this.securityKeyBuilder = securityKeyBuilder;
        return this;
    }

    public DlmsDevice buildDlmsDevice(final Map<String, String> inputSettings) {
        final DlmsDevice dlmsDevice = new DlmsDevice();
        if (inputSettings.containsKey(Keys.KEY_DEVICE_IDENTIFICATION)) {
            dlmsDevice.setDeviceIdentification((inputSettings.get(Keys.KEY_DEVICE_IDENTIFICATION)));
        }
        if (inputSettings.containsKey(Keys.KEY_VERSION)) {
            dlmsDevice.setVersion(Long.parseLong(inputSettings.get(Keys.KEY_VERSION)));
        }
        if (inputSettings.containsKey(Keys.KEY_ICC_ID)) {
            dlmsDevice.setIccId((inputSettings.get(Keys.KEY_ICC_ID)));
        }
        if (inputSettings.containsKey(Keys.KEY_COMMUNICATION_PROVIDER)) {
            dlmsDevice.setCommunicationProvider((inputSettings.get(Keys.KEY_COMMUNICATION_PROVIDER)));
        }
        if (inputSettings.containsKey(Keys.KEY_COMMUNICATION_METHOD)) {
            dlmsDevice.setCommunicationMethod((inputSettings.get(Keys.KEY_COMMUNICATION_METHOD)));
        }
        if (inputSettings.containsKey(Keys.KEY_HLS3ACTIVE)) {
            dlmsDevice.setHls3Active(Boolean.parseBoolean(inputSettings.get(Keys.KEY_HLS3ACTIVE)));
        }
        if (inputSettings.containsKey(Keys.KEY_HLS4ACTIVE)) {
            dlmsDevice.setHls4Active(Boolean.parseBoolean(inputSettings.get(Keys.KEY_HLS4ACTIVE)));
        }
        if (inputSettings.containsKey(Keys.KEY_HLS5ACTIVE)) {
            dlmsDevice.setHls5Active(Boolean.parseBoolean(inputSettings.get(Keys.KEY_HLS5ACTIVE)));
        }
        if (inputSettings.containsKey(Keys.KEY_CHALLENGE_LENGTH)) {
            dlmsDevice.setChallengeLength(Integer.parseInt(inputSettings.get(Keys.KEY_CHALLENGE_LENGTH)));
        }
        if (inputSettings.containsKey(Keys.KEY_WITH_LIST_SUPPORTED)) {
            dlmsDevice.setWithListSupported(Boolean.parseBoolean(inputSettings.get(Keys.KEY_WITH_LIST_SUPPORTED)));
        }
        if (inputSettings.containsKey(Keys.KEY_SELECTIVE_ACCESS_SUPPORTED)) {
            dlmsDevice.setSelectiveAccessSupported(
                    Boolean.parseBoolean(inputSettings.get(Keys.KEY_SELECTIVE_ACCESS_SUPPORTED)));
        }
        if (inputSettings.containsKey(Keys.KEY_IP_ADDRESS_IS_STATIC)) {
            dlmsDevice.setIpAddressIsStatic(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IP_ADDRESS_IS_STATIC)));
        }
        if (inputSettings.containsKey(Keys.KEY_PORT)) {
            dlmsDevice.setPort(Long.parseLong(inputSettings.get(Keys.KEY_PORT)));
        }
        if (inputSettings.containsKey(Keys.KEY_CLIENT_ID)) {
            dlmsDevice.setClientId(Long.parseLong(inputSettings.get(Keys.KEY_CLIENT_ID)));
        }
        if (inputSettings.containsKey(Keys.KEY_LOGICAL_ID)) {
            dlmsDevice.setLogicalId(Long.parseLong(inputSettings.get(Keys.KEY_LOGICAL_ID)));
        }
        if (inputSettings.containsKey(Keys.KEY_IN_DEBUG_MODE)) {
            dlmsDevice.setInDebugMode(Boolean.parseBoolean(inputSettings.get(Keys.KEY_IN_DEBUG_MODE)));
        }

        return dlmsDevice;
    }

}
