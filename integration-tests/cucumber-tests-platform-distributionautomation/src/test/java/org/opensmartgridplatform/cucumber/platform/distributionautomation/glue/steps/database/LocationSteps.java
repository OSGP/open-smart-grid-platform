/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.distributionautomation.glue.steps.database;

import io.cucumber.java.en.Given;
import java.util.Map;
import javax.persistence.EntityNotFoundException;
import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Location;
import org.opensmartgridplatform.adapter.kafka.da.domain.repositories.LocationRepository;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.springframework.beans.factory.annotation.Autowired;

public class LocationSteps {

  @Autowired private LocationRepository locationRepository;

  @Given("a location")
  public void givenALocation(final Map<String, String> settings) {
    final Location location = new Location();
    location.setSubstationIdentification(
        ReadSettingsHelper.getString(
            settings,
            PlatformDistributionAutomationKeys.SUBSTATION_IDENTIFICATION,
            PlatformDistributionAutomationKeys.SUBSTATION_IDENTIFICATION));
    location.setName(
        ReadSettingsHelper.getString(
            settings,
            PlatformDistributionAutomationKeys.SUBSTATION_NAME,
            PlatformDistributionAutomationDefaults.SUBSTATION_NAME));

    this.locationRepository.save(location);
  }

  public Location findLocation(final String substationIdentification) {
    return this.locationRepository
        .findOneBySubstationIdentification(substationIdentification)
        .orElseThrow(() -> new EntityNotFoundException("Location not found."));
  }
}
