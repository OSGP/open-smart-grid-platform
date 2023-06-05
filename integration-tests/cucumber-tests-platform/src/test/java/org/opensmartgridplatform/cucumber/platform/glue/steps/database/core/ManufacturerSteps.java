// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getBoolean;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformDefaults;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.cucumber.platform.core.builders.ManufacturerBuilder;
import org.opensmartgridplatform.domain.core.entities.Manufacturer;
import org.opensmartgridplatform.domain.core.repositories.ManufacturerRepository;
import org.springframework.beans.factory.annotation.Autowired;

/** The manufacturer related steps. */
public class ManufacturerSteps {

  @Autowired private ManufacturerRepository manufacturerRepository;

  /**
   * Generic method which adds a manufacturer using the settings.
   *
   * @param settings The settings for the manufacturer to be used.
   * @throws Throwable
   */
  @Given("^a manufacturer$")
  public void aManufacturer(final Map<String, String> settings) throws Throwable {

    final Manufacturer manufacturer = new ManufacturerBuilder().withSettings(settings).build();

    this.manufacturerRepository.save(manufacturer);
  }

  /**
   * Verify whether the entity is created as expected.
   *
   * @throws Throwable
   */
  @Then("^the entity manufacturer exists$")
  public void theEntityManufacturerExists(final Map<String, String> settings) throws Throwable {
    final Manufacturer manufacturer =
        this.manufacturerRepository.findByCode(
            getString(
                settings,
                PlatformKeys.MANUFACTURER_CODE,
                PlatformDefaults.DEFAULT_MANUFACTURER_CODE));

    assertThat(manufacturer.getName())
        .isEqualTo(
            getString(
                settings,
                PlatformKeys.MANUFACTURER_NAME,
                PlatformDefaults.DEFAULT_MANUFACTURER_NAME));

    assertThat(manufacturer.isUsePrefix())
        .isEqualTo(
            getBoolean(
                settings,
                PlatformKeys.MANUFACTURER_USE_PREFIX,
                PlatformDefaults.DEFAULT_MANUFACTURER_USE_PREFIX));
  }
}
