/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Date;
import java.util.Map;

import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.cucumber.platform.core.builders.CucumberBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretStatus;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;

public class SecretBuilder implements CucumberBuilder<DbEncryptedSecret> {

    private boolean builderEnabled = false;

    private SecretType secretType;
    private String key = PlatformSmartmeteringDefaults.SECURITY_KEY_A_DB;
    private SecretStatus status;
    private String deviceIdentification;
    private Date creationTime;

    public SecretBuilder setSecretType(SecretType secretType) {
        this.secretType = secretType;
        return this;
    }

    public SecretType getSecretType() {
        return this.secretType;
    }

    public SecretBuilder setSecurityKeyType(final SecurityKeyType securityKeyType) {
        this.secretType = SecretType.valueOf(securityKeyType.toSecretType().value());
        return this;
    }

    public SecurityKeyType getSecurityKeyType() {
        return SecurityKeyType.fromSecretType(
                org.opensmartgridplatform.ws.schema.core.secret.management.SecretType.valueOf(this.secretType.name()));
    }

    public SecretBuilder setKey(final String key) {
        this.key = key;
        return this;
    }

    public SecretBuilder setDlmsDevice(final DlmsDevice dlmsDevice) {
        this.deviceIdentification = dlmsDevice.getDeviceIdentification();
        return this;
    }

    public SecretBuilder setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
        return this;
    }

    public SecretBuilder setSecretStatus(final SecretStatus status) {
        this.status = status;
        return this;
    }

    public SecretBuilder setCreationTime(final Date creationTime) {
        this.creationTime = creationTime;
        return this;
    }

    @Override
    public SecretBuilder withSettings(final Map<String, String> inputSettings) {
        if (SecurityKeyType.E_METER_AUTHENTICATION == this.getSecurityKeyType() && inputSettings
                .containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_AUTHENTICATIONKEY)) {
            this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTIONKEY));
        }

        if (SecurityKeyType.E_METER_MASTER == this.getSecurityKeyType() && inputSettings
                .containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_MASTERKEY)) {
            this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_MASTERKEY));
        }

        if (SecurityKeyType.E_METER_ENCRYPTION == this.getSecurityKeyType() && inputSettings
                .containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTIONKEY)) {
            this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTIONKEY));
        }

        if (SecurityKeyType.G_METER_MASTER == this.getSecurityKeyType() && inputSettings
                .containsKey(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY)) {
            this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY));
        }

        if (SecurityKeyType.G_METER_ENCRYPTION == this.getSecurityKeyType() && inputSettings
                .containsKey(PlatformSmartmeteringKeys.MBUS_USER_KEY)) {
            this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.MBUS_USER_KEY));
        }

        if (SecurityKeyType.PASSWORD == this.getSecurityKeyType() && inputSettings
                .containsKey(PlatformSmartmeteringKeys.PASSWORD)) {
            this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.PASSWORD));
        }

        return this;
    }

    @Override
    public DbEncryptedSecret build() {
        final DbEncryptedSecret securityKey = new DbEncryptedSecret();
        securityKey.setDeviceIdentification(this.deviceIdentification);
        securityKey.setSecretType(this.secretType);
        securityKey.setEncodedSecret(this.key);
        securityKey.setSecretStatus(this.status == null ? SecretStatus.ACTIVE : this.status);
        securityKey.setCreationTime(this.creationTime == null ? new Date() : this.creationTime);
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
