/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringmanagement;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventLogCategory;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.management.EventType;

public class FindPowerQualityExtendedEventsReads extends AbstractFindEventsReads {

  private static final List<EventType> allowed =
      Collections.unmodifiableList(
          Arrays.asList(
              EventType.VOLTAGE_SAG_IN_PHASE_L1,
              EventType.VOLTAGE_SAG_IN_PHASE_L2,
              EventType.VOLTAGE_SAG_IN_PHASE_L3,
              EventType.VOLTAGE_SWELL_IN_PHASE_L1,
              EventType.VOLTAGE_SWELL_IN_PHASE_L2,
              EventType.VOLTAGE_SWELL_IN_PHASE_L3));

  @Override
  protected String getEventLogCategory() {
    return EventLogCategory.POWER_QUALITY_EXTENDED_EVENT_LOG.name();
  }

  @When("^receiving a find power quality extended events request$")
  @Override
  public void receivingAFindEventsRequest(final Map<String, String> requestData) throws Throwable {
    super.receivingAFindEventsRequest(requestData);
  }

  @Then("^power quality extended events should be returned$")
  @Override
  public void eventsShouldBeReturned(final Map<String, String> settings) throws Throwable {
    super.eventsShouldBeReturned(settings);
  }

  @Then("^power quality extended events for all types should be returned$")
  public void standardEventsForAllTypesShouldBeReturned(final Map<String, String> settings)
      throws Throwable {
    super.eventsForAllTypesShouldBeReturned(settings);
  }

  @Then("^(\\d++) power quality extended events should be returned$")
  public void numberOfEventsShouldBeReturned(
      final int numberOfEvents, final Map<String, String> settings) throws Throwable {
    super.eventsShouldBeReturned(numberOfEvents, settings);
  }

  @Override
  protected List<EventType> getAllowedEventTypes() {
    return allowed;
  }
}
