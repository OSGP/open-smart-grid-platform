/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.builders.entities;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;

import com.alliander.osgp.cucumber.platform.core.builders.CucumberBuilder;
import com.alliander.osgp.cucumber.platform.helpers.UtcDateHelper;
import com.alliander.osgp.cucumber.platform.inputparsers.DateInputParser;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;

public class SecurityKeyBuilder implements CucumberBuilder<SecurityKey> {

    private boolean builderEnabled = false;

    private SecurityKeyType securityKeyType = null;
    private Date validFrom = new DateTime(UtcDateHelper.getUtcDate()).minusDays(1).toDate();
    private Date validTo = PlatformSmartmeteringDefaults.VALID_TO;
    private Long version = PlatformSmartmeteringDefaults.VERSION;
    private String key = PlatformSmartmeteringDefaults.SECURITY_KEY_A_DB;

    private DlmsDevice dlmsDevice;

    public SecurityKeyBuilder setSecurityKeyType(final SecurityKeyType securityKeyType) {
        this.securityKeyType = securityKeyType;
        return this;
    }

    public SecurityKeyBuilder setValidFrom(final Date validFrom) {
        this.validFrom = validFrom;
        return this;
    }

    public SecurityKeyBuilder setValidTo(final Date validTo) {
        this.validTo = validTo;
        return this;
    }

    public SecurityKeyBuilder setVersion(final Long version) {
        this.version = version;
        return this;
    }

    public SecurityKeyBuilder setKey(final String key) {
        this.key = key;
        return this;
    }

    public SecurityKeyBuilder setDlmsDevice(final DlmsDevice dlmsDevice) {
        this.dlmsDevice = dlmsDevice;
        return this;
    }

    @Override
    public SecurityKeyBuilder withSettings(final Map<String, String> inputSettings) {
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.VERSION)) {
            this.setVersion(Long.parseLong(inputSettings.get(PlatformSmartmeteringKeys.VERSION)));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.VALID_FROM)) {
            this.setValidFrom(DateInputParser.parse(inputSettings.get(PlatformSmartmeteringKeys.VALID_FROM)));
        }
        if (inputSettings.containsKey(PlatformSmartmeteringKeys.VALID_TO)) {
            this.setValidTo(DateInputParser.parse(inputSettings.get(PlatformSmartmeteringKeys.VALID_TO)));
        }

        if (SecurityKeyType.E_METER_AUTHENTICATION == this.securityKeyType
                && inputSettings.containsKey(PlatformSmartmeteringKeys.SECURITY_KEY_A)) {
            this.setKey(inputSettings.get(PlatformSmartmeteringKeys.SECURITY_KEY_A));
        }

        if (SecurityKeyType.E_METER_MASTER == this.securityKeyType
                && inputSettings.containsKey(PlatformSmartmeteringKeys.SECURITY_KEY_M)) {
            this.setKey(inputSettings.get(PlatformSmartmeteringKeys.SECURITY_KEY_M));
        }

        if (SecurityKeyType.E_METER_ENCRYPTION == this.securityKeyType
                && inputSettings.containsKey(PlatformSmartmeteringKeys.SECURITY_KEY_E)) {
            this.setKey(inputSettings.get(PlatformSmartmeteringKeys.SECURITY_KEY_E));
        }

        if (SecurityKeyType.G_METER_MASTER == this.securityKeyType
                && inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY)) {
            this.setKey(inputSettings.get(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY));
        }

        if (SecurityKeyType.G_METER_ENCRYPTION == this.securityKeyType
                && inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_USER_KEY)) {
            this.setKey(inputSettings.get(PlatformSmartmeteringKeys.MBUS_USER_KEY));
        }

        if (SecurityKeyType.PASSWORD == this.securityKeyType
                && inputSettings.containsKey(PlatformSmartmeteringKeys.PASSWORD)) {
            this.setKey(inputSettings.get(PlatformSmartmeteringKeys.PASSWORD));
        }

        return this;
    }

    @Override
    public SecurityKey build() {
        final SecurityKey securityKey = new SecurityKey(this.dlmsDevice, this.securityKeyType, this.key, this.validFrom,
                this.validTo);

        securityKey.setVersion(this.version);
        securityKey.setValidFrom(this.validFrom);
        securityKey.setValidTo(this.validTo);

        return securityKey;
    }

    public boolean enabled() {
        return this.builderEnabled;
    }

    public void disable() {
        this.builderEnabled = false;
    }

    public void enable() {
        this.builderEnabled = true;
    }
}
