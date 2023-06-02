//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.core.builders.DeviceModelBuilder;
import org.opensmartgridplatform.domain.core.entities.DeviceModel;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.repositories.DeviceModelRepository;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class DeviceModelSteps {

  @Autowired private DeviceModelRepository deviceModelRepository;

  @Autowired private ManufacturerRepository manufacturerRepository;

  /**
   * Generic method which adds a device model using the settings.
   *
   * @param settings The settings for the device model to be used.
   */
  @Given("^a device model")
  public DeviceModel aDeviceModel(final Map<String, String> settings) {
    return this.insertDeviceModel(settings);
  }

  /**
   * Generic method to check if the device model is created as expected in the database.
   *
   * @param expectedEntity The expected settings.
   * @throws Throwable
   */
  @Then("^the entity device model exists$")
  public void theEntityDeviceModelExists(final Map<String, String> expectedEntity)
      throws Throwable {
    final String modelCode =
        getString(
            expectedEntity,
            PlatformKeys.KEY_DEVICE_MODEL_MODELCODE,
            PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE);
    final String modelDescription =
        getString(
            expectedEntity,
            PlatformKeys.KEY_DEVICE_MODEL_DESCRIPTION,
            PlatformDefaults.DEFAULT_DEVICE_MODEL_DESCRIPTION);

    final Manufacturer manufacturer =
        this.manufacturerRepository.findByCode(
            getString(
                expectedEntity,
                PlatformKeys.MANUFACTURER_CODE,
                PlatformDefaults.DEFAULT_MANUFACTURER_CODE));

    final List<DeviceModel> entityList =
        this.deviceModelRepository.findByManufacturer(manufacturer);

    for (final DeviceModel deviceModel : entityList) {
      if (deviceModel.getModelCode().equals(modelCode)) {
        assertThat(deviceModel.getDescription()).isEqualTo(modelDescription);
        return;
      }
    }
    Assertions.fail();
  }

  /**
   * Generic method to check if the device model is NOT created as expected in the database.
   *
   * @param entity The settings.
   * @throws Throwable
   */
  @Then("^the entity device model does not exist$")
  public void theEntityDeviceModelDoesNotExists(final Map<String, String> entity) throws Throwable {
    final String modelCode =
        getString(
            entity,
            PlatformKeys.KEY_DEVICE_MODEL_MODELCODE,
            PlatformDefaults.DEFAULT_DEVICE_MODEL_MODEL_CODE);

    final Manufacturer manufacturer =
        this.manufacturerRepository.findByCode(
            getString(
                entity,
                PlatformKeys.MANUFACTURER_CODE,
                PlatformDefaults.DEFAULT_MANUFACTURER_CODE));

    final List<DeviceModel> entityList =
        this.deviceModelRepository.findByManufacturer(manufacturer);

    for (final DeviceModel deviceModel : entityList) {
      assertThat(deviceModel.getModelCode()).isNotEqualTo(modelCode);
    }
  }

  /** This inserts a default DeviceModel */
  public DeviceModel insertDeviceModel(final Map<String, String> settings) {

    final Manufacturer manufacturer =
        this.manufacturerRepository.findByName(
            getString(
                settings,
                PlatformKeys.MANUFACTURER_NAME,
                PlatformDefaults.DEFAULT_MANUFACTURER_NAME));
    final DeviceModel deviceModel =
        new DeviceModelBuilder().withSettings(settings).withManufacturer(manufacturer).build();
    return this.deviceModelRepository.save(deviceModel);
  }
}
