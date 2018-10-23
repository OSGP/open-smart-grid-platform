/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import java.util.Date;
import java.util.Map;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.core.factories.RelayStatusFactory;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.springframework.beans.factory.annotation.Autowired;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

public class RelayStatusSteps extends BaseDeviceSteps {

    @Autowired
    private SsldDeviceSteps ssldDeviceSteps;

    @Autowired
    private RelayStatusFactory relayStatusFactory;

    @Given("^a device relay status$")
    public void aDeviceRelayStatus(final Map<String, String> settings) {
        final Ssld ssld = this.ssldDeviceSteps.findByDeviceIdentification(settings);
        final RelayStatus relayStatus = this.relayStatusFactory.fromMap(ssld, settings);

        ssld.addOrUpdateRelayStatus(relayStatus);
        this.ssldDeviceSteps.saveSsld(ssld);
    }

    @Then("^there is a device relay status$")
    public void thereIsADeviceRelayStatus(final Map<String, String> settings) {
        final RelayStatus expected = this.relayStatusFactory.fromMap(settings);
        Wait.until(() -> {
            final RelayStatus actual = this.relayStatusFactory.fromDb(settings);

            this.assertLastSwitchingEventEquals(expected, actual);
            this.assertLastKnownStateEquals(expected, actual);
        });
    }

    @Then("^there is a device relay status with a recent last known state time$")
    public void thereIsADeviceRelayStatusWithARecentLastKnownTime(final Map<String, String> settings) {
        final RelayStatus expected = this.relayStatusFactory.fromMap(settings);

        Wait.until(() -> {
            final RelayStatus actual = this.relayStatusFactory.fromDb(settings);

            this.assertLastSwitchingEventEquals(expected, actual);

            // Check if the last known state time is at most 3 minutes in the
            // past
            final Date startDate = DateTime.now().minusMinutes(3).toDate();
            Assert.assertTrue(actual.getLastKnownStateTime().after(startDate));
            Assert.assertEquals(expected.isLastKnownState(), actual.isLastKnownState());
        });
    }

    private void assertLastSwitchingEventEquals(final RelayStatus expected, final RelayStatus actual) {
        if (expected != null && expected.getLastSwitchingEventTime() != null) {
            Assert.assertNotNull(actual);
            Assert.assertEquals(expected.getLastSwitchingEventTime(), actual.getLastSwitchingEventTime());
            Assert.assertEquals(expected.isLastSwitchingEventState(), actual.isLastSwitchingEventState());
        }
    }

    private void assertLastKnownStateEquals(final RelayStatus expected, final RelayStatus actual) {
        if (expected != null && expected.getLastKnownStateTime() != null) {
            Assert.assertNotNull(actual);
            Assert.assertEquals(expected.getLastKnownStateTime(), actual.getLastKnownStateTime());
            Assert.assertEquals(expected.isLastKnownState(), actual.isLastKnownState());
        }
    }
}
