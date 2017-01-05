/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.builders.entities;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.osgp.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKey;
import org.osgp.adapter.protocol.dlms.domain.entities.SecurityKeyType;

import com.alliander.osgp.platform.cucumber.helpers.UtcDateHelper;
import com.alliander.osgp.platform.cucumber.inputparsers.DateInputParser;
import com.alliander.osgp.platform.dlms.cucumber.steps.Defaults;
import com.alliander.osgp.platform.dlms.cucumber.steps.Keys;

public class SecurityKeyBuilder implements CucumberBuilder<SecurityKey> {

    private boolean builderEnabled = true;

    private SecurityKeyType securityKeyType = null;
    private Date validFrom = new DateTime(UtcDateHelper.getUtcDate()).minusDays(1).toDate();
    private Date validTo = Defaults.VALID_TO;
    private Long version = Defaults.VERSION;
    private String key = Defaults.SECURITY_KEY_A;

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
        if (inputSettings.containsKey(Keys.VERSION)) {
            this.setVersion(Long.parseLong(inputSettings.get(Keys.VERSION)));
        }
        if (inputSettings.containsKey(Keys.VALID_FROM)) {
            this.setValidFrom(DateInputParser.parse(inputSettings.get(Keys.VALID_FROM)));
        }
        if (inputSettings.containsKey(Keys.VALID_TO)) {
            this.setValidTo(DateInputParser.parse(inputSettings.get(Keys.VALID_TO)));
        }

        if (inputSettings.containsKey(Keys.SECURITY_KEY_A)) {
            this.setKey(inputSettings.get(Keys.SECURITY_KEY_A));
        }

        if (inputSettings.containsKey(Keys.SECURITY_KEY_M)) {
            this.setKey(inputSettings.get(Keys.SECURITY_KEY_M));
        }

        if (inputSettings.containsKey(Keys.SECURITY_KEY_E)) {
            this.setKey(inputSettings.get(Keys.SECURITY_KEY_E));
        }

        return this;
    }

    @Override
    public SecurityKey build() {
        final SecurityKey securityKey = new SecurityKey(this.dlmsDevice, this.securityKeyType, this.key,
                this.validFrom, this.validTo);

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
