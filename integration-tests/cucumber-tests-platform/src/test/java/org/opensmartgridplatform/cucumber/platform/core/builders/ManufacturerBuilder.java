/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.core.builders;

import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;

public class ManufacturerBuilder implements CucumberBuilder<Manufacturer> {

  private String code = PlatformDefaults.DEFAULT_MANUFACTURER_CODE;
  private String name = PlatformDefaults.DEFAULT_MANUFACTURER_NAME;
  private boolean usePrefix = PlatformDefaults.DEFAULT_MANUFACTURER_USE_PREFIX;

  public ManufacturerBuilder withCode(final String code) {
    this.code = code;
    return this;
  }

  public ManufacturerBuilder withName(final String name) {
    this.name = name;
    return this;
  }

  public ManufacturerBuilder withUsePrefix(final boolean usePrefix) {
    this.usePrefix = usePrefix;
    return this;
  }

  @Override
  public Manufacturer build() {
    final Manufacturer manufacturer = new Manufacturer();
    manufacturer.setCode(this.code);
    manufacturer.setName(this.name);
    manufacturer.setUsePrefix(this.usePrefix);
    return manufacturer;
  }

  @Override
  public ManufacturerBuilder withSettings(final Map<String, String> inputSettings) {

    if (inputSettings.containsKey(PlatformKeys.MANUFACTURER_CODE)) {
      this.withCode(inputSettings.get(PlatformKeys.MANUFACTURER_CODE));
    }

    if (inputSettings.containsKey(PlatformKeys.MANUFACTURER_NAME)) {
      this.withName(inputSettings.get(PlatformKeys.MANUFACTURER_NAME));
    }

    if (inputSettings.containsKey(PlatformKeys.MANUFACTURER_USE_PREFIX)) {
      this.withUsePrefix(
          Boolean.parseBoolean(inputSettings.get(PlatformKeys.MANUFACTURER_USE_PREFIX)));
    }

    return this;
  }
}
