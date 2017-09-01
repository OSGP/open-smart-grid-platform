/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.smartmetering.glue.steps.ws.smartmetering.smartmeteringbundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.ActionResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetActivityCalendarRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.bundle.SetConfigurationObjectRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.common.Response;
import com.alliander.osgp.cucumber.core.ScenarioContext;
import com.alliander.osgp.cucumber.platform.helpers.SettingsHelper;
import com.alliander.osgp.cucumber.platform.smartmetering.PlatformSmartmeteringKeys;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetActivityCalendarRequestBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.SetConfigurationObjectRequestBuilder;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.ActivityCalendar;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.DayProfile;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.DayProfileAction;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.SeasonProfile;
import com.alliander.osgp.cucumber.platform.smartmetering.support.ws.smartmetering.bundle.activitycalendar.WeekProfile;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class BundledSetActivityCalendarSteps extends BaseBundleSteps {

    @Given("^an activity calendar$")
    public void anActivityCalendar(final Map<String, String> parameters) throws Throwable {
        final String name = parameters.get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR_NAME);
        final String activatePassiveCalendarTime = parameters
                .get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR_ACTIVATE_PASSIVE_CALENDAR_TIME);

        final ActivityCalendar activityCalendar = new ActivityCalendar(name, activatePassiveCalendarTime);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR, activityCalendar);
    }

    @Given("^the activity calendar contains a season profile$")
    public void theActivityCalendarContainsASeasonProfile(final Map<String, String> parameters) throws Throwable {

        final ActivityCalendar activityCalendar = (ActivityCalendar) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR);

        final String name = parameters.get(PlatformSmartmeteringKeys.SEASON_PROFILE_NAME);
        final String start = parameters.get(PlatformSmartmeteringKeys.SEASON_PROFILE_START);
        final String weekName = parameters.get(PlatformSmartmeteringKeys.SEASON_PROFILE_WEEK_NAME);

        final SeasonProfile profile = new SeasonProfile(name, start, weekName);

        activityCalendar.getSeasonProfiles().put(name, profile);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR, activityCalendar);
    }

    @Given("^the activity calendar contains a week profile$")
    public void theActivityCalendarContainsAWeekProfile(final Map<String, String> parameters) throws Throwable {

        final ActivityCalendar activityCalendar = (ActivityCalendar) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR);

        final String name = parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_NAME);
        final int monday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_MONDAY_DAY_ID));
        final int tuesday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_TUESDAY_DAY_ID));
        final int wednesday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_WEDNESDAY_DAY_ID));
        final int thursday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_THURSDAY_DAY_ID));
        final int friday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_FRIDAY_DAY_ID));
        final int saturday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_SATURDAY_DAY_ID));
        final int sunday = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.WEEK_PROFILE_SUNDAY_DAY_ID));

        final WeekProfile profile = new WeekProfile(name, monday, tuesday, wednesday, thursday, friday, saturday,
                sunday);

        activityCalendar.getWeekProfiles().put(name, profile);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR, activityCalendar);

    }

    @Given("^the activity calendar contains a day profile$")
    public void theActivityCalendarContainsADayProfile(final Map<String, String> parameters) throws Throwable {

        final ActivityCalendar activityCalendar = (ActivityCalendar) ScenarioContext.current()
                .get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR);

        final int dayId = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.DAY_PROFILE_DAY_ID));
        final int actionCount = Integer.parseInt(parameters.get(PlatformSmartmeteringKeys.DAY_PROFILE_ACTION_COUNT));

        final DayProfile profile = new DayProfile(dayId);

        for (int i = 1; i <= actionCount; i++) {

            final String start = SettingsHelper.getStringValue(parameters,
                    PlatformSmartmeteringKeys.DAY_PROFILE_START_TIME, i);
            final int selector = SettingsHelper.getIntegerValue(parameters,
                    PlatformSmartmeteringKeys.DAY_PROFILE_SCRIPT_SELECTOR, i);

            profile.getDayProfileActions().add(new DayProfileAction(start, selector));
        }

        activityCalendar.getDayProfiles().put(dayId, profile);

        ScenarioContext.current().put(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR, activityCalendar);

    }

    @Given("^the bundle request contains a set activity calendar action$")
    public void theBundleRequestContainsASetActivityCalendarAction() throws Throwable {

        SetActivityCalendarRequest action;
        if (ScenarioContext.current().get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR) == null) {

            action = new SetActivityCalendarRequestBuilder().withDefaults().build();

        } else {
            final ActivityCalendar activityCalendar = (ActivityCalendar) ScenarioContext.current()
                    .get(PlatformSmartmeteringKeys.ACTIVITY_CALENDAR);
            action = new SetActivityCalendarRequestBuilder().withActivityCalendar(activityCalendar).build();

        }

        this.addActionToBundleRequest(action);
    }

    @Given("^the bundle request contains a set activity calendar action with parameters$")
    public void theBundleRequestContainsASetConfigurationObjectAction(final Map<String, String> parameters)
            throws Throwable {

        final SetConfigurationObjectRequest action = new SetConfigurationObjectRequestBuilder()
                .fromParameterMap(parameters).build();

        this.addActionToBundleRequest(action);
    }

    @Then("^the bundle response should contain a set activity calendar response$")
    public void theBundleResponseShouldContainASetActivityCalendarResponse() throws Throwable {
        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
    }

    @Then("^the bundle response should contain a set activity calendar response with values$")
    public void theBundleResponseShouldContainASetActivityCalendarResponse(final Map<String, String> values)
            throws Throwable {

        final Response response = this.getNextBundleResponse();

        assertTrue("Not a valid response", response instanceof ActionResponse);
        assertEquals("Result is not as expected.", values.get(PlatformSmartmeteringKeys.RESULT),
                response.getResult().name());
    }

}
