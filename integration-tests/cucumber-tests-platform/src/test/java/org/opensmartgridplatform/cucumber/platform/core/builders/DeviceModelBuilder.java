// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.core.builders;

import java.util.Collections;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;

public class DeviceModelBuilder implements CucumberBuilder<DeviceModel> {

  /*
   * Build the default manufacturer with an empty map of settings as input, so
   * the default manufacturer properties will be applied.
   */
  private Manufacturer manufacturer =
      new ManufacturerBuilder().withSettings(Collections.emptyMap()).build();
  private String modelCode = PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE;
  private String description = PlatformDefaults.DEFAULT_DEVICE_MODEL_DESCRIPTION;
  private boolean fileStorage = PlatformDefaults.DEFAULT_FILESTORAGE;

  public DeviceModelBuilder withManufacturer(final Manufacturer manufacturer) {
    this.manufacturer = manufacturer;
    return this;
  }

  public DeviceModelBuilder withModelCode(final String modelCode) {
    this.modelCode = modelCode;
    return this;
  }

  public DeviceModelBuilder withDescription(final String description) {
    this.description = description;
    return this;
  }

  public DeviceModelBuilder withFileStorage(final boolean fileStorage) {
    this.fileStorage = fileStorage;
    return this;
  }

  @Override
  public DeviceModel build() {
    return new DeviceModel(this.manufacturer, this.modelCode, this.description, this.fileStorage);
  }

  @Override
  public DeviceModelBuilder withSettings(final Map<String, String> inputSettings) {

    if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_MODELCODE)) {
      this.withModelCode(inputSettings.get(PlatformKeys.DEVICEMODEL_MODELCODE));
    }

    if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_DESCRIPTION)) {
      this.withDescription(inputSettings.get(PlatformKeys.DEVICEMODEL_DESCRIPTION));
    }

    if (inputSettings.containsKey(PlatformKeys.DEVICEMODEL_FILESTORAGE)) {
      this.withFileStorage(
          Boolean.parseBoolean(inputSettings.get(PlatformKeys.DEVICEMODEL_FILESTORAGE)));
    }

    return this;
  }
}
