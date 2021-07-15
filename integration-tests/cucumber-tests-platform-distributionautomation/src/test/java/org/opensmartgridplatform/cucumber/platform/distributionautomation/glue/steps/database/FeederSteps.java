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
import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Feeder;
import org.opensmartgridplatform.adapter.kafka.da.domain.entities.Location;
import org.opensmartgridplatform.adapter.kafka.da.domain.repositories.FeederRepository;
import org.opensmartgridplatform.cucumber.core.ReadSettingsHelper;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationDefaults;
import org.opensmartgridplatform.cucumber.platform.distributionautomation.PlatformDistributionAutomationKeys;
import org.springframework.beans.factory.annotation.Autowired;

public class FeederSteps {

  @Autowired private LocationSteps locationSteps;

  @Autowired private FeederRepository feederRepository;

  @Given("a feeder")
  public void givenAFeeder(final Map<String, String> settings) {
    final Feeder feeder = new Feeder();
    final String substationIdentification =
        ReadSettingsHelper.getString(
            settings,
            PlatformDistributionAutomationKeys.SUBSTATION_IDENTIFICATION,
            PlatformDistributionAutomationDefaults.SUBSTATION_IDENTIFICATION);
    final Location location = this.locationSteps.findLocation(substationIdentification);
    feeder.setLocation(location);
    feeder.setFeederNumber(
        ReadSettingsHelper.getInteger(
            settings,
            PlatformDistributionAutomationKeys.FEEDER_NUMBER,
            PlatformDistributionAutomationDefaults.FEEDER_NUMBER));
    feeder.setName(
        ReadSettingsHelper.getString(
            settings,
            PlatformDistributionAutomationKeys.FEEDER_NAME,
            PlatformDistributionAutomationDefaults.FEEDER_NAME));

    this.feederRepository.save(feeder);
  }
}
