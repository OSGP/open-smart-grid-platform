/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.common.glue.steps.ws.core.devicemanagement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_DEVICE_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_MESSAGE_TYPE;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_ORGANIZATION_IDENTIFICATION;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.KEY_SCHEDULED_TIME;
import static org.opensmartgridplatform.cucumber.platform.PlatformKeys.RESPONSE;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.xml.datatype.XMLGregorianCalendar;
import org.joda.time.DateTime;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindScheduledTasksRequest;
import org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.FindScheduledTasksResponse;
import org.opensmartgridplatform.cucumber.core.DateTimeHelper;
import org.opensmartgridplatform.cucumber.core.ScenarioContext;
import org.opensmartgridplatform.cucumber.platform.common.support.ws.core.CoreDeviceManagementClient;
import org.opensmartgridplatform.cucumber.platform.glue.steps.database.core.ScheduledTaskSteps;
import org.opensmartgridplatform.shared.exceptionhandling.WebServiceSecurityException;
import org.springframework.beans.factory.annotation.Autowired;

public class FindScheduledTasksSteps {

  @Autowired private ScheduledTaskSteps scheduledTaskSteps;

  @Autowired private CoreDeviceManagementClient client;

  @Given("scheduled tasks")
  public void givenScheduleTasks(final DataTable tasks) {
    final List<Map<String, String>> scheduledTasks = tasks.asMaps(String.class, String.class);
    scheduledTasks.stream()
        .forEach(m -> this.scheduledTaskSteps.givenAScheduledTask(m.get(KEY_MESSAGE_TYPE), m));
  }

  @When("receiving a find scheduled tasks request")
  public void whenReceivingAFindScheduledTasksRequests() {
    final FindScheduledTasksRequest request = new FindScheduledTasksRequest();
    try {
      final FindScheduledTasksResponse response = this.client.findScheduledTasks(request);
      ScenarioContext.current().put(RESPONSE, response);
    } catch (final WebServiceSecurityException e) {
      ScenarioContext.current().put(RESPONSE, e);
    }
  }

  @Then("the find scheduled tasks response should contain scheduled tasks")
  public void thenTheFindScheduledTasksResponseShouldContain(final DataTable tasks) {
    final FindScheduledTasksResponse response =
        (FindScheduledTasksResponse) ScenarioContext.current().get(RESPONSE);

    final List<Map<String, String>> scheduledTasks = tasks.asMaps(String.class, String.class);

    assertThat(response.getScheduledTask().size())
        .as("Lists of scheduled tasks have different sizes.")
        .isEqualTo(scheduledTasks.size());

    for (final Map<String, String> map : scheduledTasks) {
      final boolean found =
          response.getScheduledTask().stream()
              .anyMatch(
                  hasOrganizationIdentification(map.get(KEY_ORGANIZATION_IDENTIFICATION))
                      .and(hasDeviceIdentification(map.get(KEY_DEVICE_IDENTIFICATION)))
                      .and(hasMessageType(map.get(KEY_MESSAGE_TYPE)))
                      .and(hasScheduledTime(map.get(KEY_SCHEDULED_TIME))));

      assertThat(found).as("No matching scheduled task found.").isTrue();
    }
  }

  private static final Predicate<
          org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask>
      hasOrganizationIdentification(final String organizationIdentification) {
    return task -> task.getOrganisationIdentification().equals(organizationIdentification);
  }

  private static final Predicate<
          org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask>
      hasDeviceIdentification(final String deviceIdentification) {
    return task -> task.getDeviceIdentification().equals(deviceIdentification);
  }

  private static final Predicate<
          org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask>
      hasMessageType(final String messageType) {
    return task -> task.getMessageType().equals(messageType);
  }

  private static final Predicate<
          org.opensmartgridplatform.adapter.ws.schema.core.devicemanagement.ScheduledTask>
      hasScheduledTime(final String scheduledTime) {
    return task -> isEqual(task.getScheduledTime(), scheduledTime);
  }

  private static final boolean isEqual(final XMLGregorianCalendar actual, final String expected) {
    final DateTime expectedDateTime =
        DateTimeHelper.shiftSystemZoneToUtc(DateTimeHelper.getDateTime(expected));
    final DateTime actualDateTime = new DateTime(actual.toGregorianCalendar());
    return actualDateTime.isEqual(expectedDateTime);
  }
}
