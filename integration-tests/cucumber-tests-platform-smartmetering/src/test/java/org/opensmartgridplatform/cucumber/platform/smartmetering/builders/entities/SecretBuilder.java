/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.builders.entities;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.DlmsDevice;
import org.opensmartgridplatform.adapter.protocol.dlms.domain.entities.SecurityKeyType;
import org.opensmartgridplatform.cucumber.platform.core.builders.CucumberBuilder;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringDefaults;
import org.opensmartgridplatform.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import org.opensmartgridplatform.secretmanagement.application.domain.DbEncryptedSecret;
import org.opensmartgridplatform.secretmanagement.application.domain.SecretType;

public class SecretBuilder implements CucumberBuilder<DbEncryptedSecret> {

  private boolean builderEnabled = false;

  private SecurityKeyType securityKeyType = null;
  private String key = PlatformSmartmeteringDefaults.SECURITY_KEY_A_DB;

  private DlmsDevice dlmsDevice;

  public SecretBuilder setSecurityKeyType(final SecurityKeyType securityKeyType) {
    this.securityKeyType = securityKeyType;
    return this;
  }

  public SecurityKeyType getSecurityKeyType() {
    return this.securityKeyType;
  }

  public SecretBuilder setKey(final String key) {
    this.key = key;
    return this;
  }

  public SecretBuilder setDlmsDevice(final DlmsDevice dlmsDevice) {
    this.dlmsDevice = dlmsDevice;
    return this;
  }

  @Override
  public SecretBuilder withSettings(final Map<String, String> inputSettings) {
    if (SecurityKeyType.E_METER_AUTHENTICATION == this.securityKeyType
        && inputSettings.containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_AUTHENTICATIONKEY)) {
      this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTIONKEY));
    }

    if (SecurityKeyType.E_METER_MASTER == this.securityKeyType
        && inputSettings.containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_MASTERKEY)) {
      this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_MASTERKEY));
    }

    if (SecurityKeyType.E_METER_ENCRYPTION == this.securityKeyType
        && inputSettings.containsKey(PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTIONKEY)) {
      this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.KEY_DEVICE_ENCRYPTIONKEY));
    }

    if (SecurityKeyType.G_METER_MASTER == this.securityKeyType
        && inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY)) {
      this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.MBUS_DEFAULT_KEY));
    }

    if (SecurityKeyType.G_METER_ENCRYPTION == this.securityKeyType
        && inputSettings.containsKey(PlatformSmartmeteringKeys.MBUS_USER_KEY)) {
      this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.MBUS_USER_KEY));
    }

    if (SecurityKeyType.PASSWORD == this.securityKeyType
        && inputSettings.containsKey(PlatformSmartmeteringKeys.PASSWORD)) {
      this.setKey(getString(inputSettings, PlatformSmartmeteringKeys.PASSWORD));
    }

    return this;
  }

  @Override
  public DbEncryptedSecret build() {
    final DbEncryptedSecret securityKey = new DbEncryptedSecret();
    if (this.dlmsDevice != null) {
      securityKey.setDeviceIdentification(this.dlmsDevice.getDeviceIdentification());
    }
    securityKey.setSecretType(SecretType.valueOf(this.securityKeyType.toSecretType().value()));
    securityKey.setEncodedSecret(this.key);
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
