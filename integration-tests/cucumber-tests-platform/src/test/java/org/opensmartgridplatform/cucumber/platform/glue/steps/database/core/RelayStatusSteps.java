// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.glue.steps.database.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.core.Wait;
import org.opensmartgridplatform.cucumber.platform.core.factories.RelayStatusFactory;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.springframework.beans.factory.annotation.Autowired;

public class RelayStatusSteps extends BaseDeviceSteps {

  @Autowired private SsldDeviceSteps ssldDeviceSteps;

  @Autowired private RelayStatusFactory relayStatusFactory;

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
    Wait.until(
        () -> {
          final RelayStatus actual = this.relayStatusFactory.fromDb(settings);

          this.assertLastSwitchingEventEquals(expected, actual);
          this.assertLastKnownStateEquals(expected, actual);
        });
  }

  @Then("^there is a device relay status with a recent last known state time$")
  public void thereIsADeviceRelayStatusWithARecentLastKnownTime(
      final Map<String, String> settings) {
    final RelayStatus expected = this.relayStatusFactory.fromMap(settings);

    Wait.until(
        () -> {
          final RelayStatus actual = this.relayStatusFactory.fromDb(settings);

          this.assertLastSwitchingEventEquals(expected, actual);

          // Check if the last known state time is at most 3 minutes in the
          // past
          final Date startDate = DateTime.now().minusMinutes(3).toDate();
          assertThat(actual.getLastKnownStateTime().after(startDate)).isTrue();
          assertThat(actual.isLastKnownState()).isEqualTo(expected.isLastKnownState());
        });
  }

  private void assertLastSwitchingEventEquals(
      final RelayStatus expected, final RelayStatus actual) {
    if (expected != null && expected.getLastSwitchingEventTime() != null) {
      assertThat(actual).isNotNull();

      final Timestamp timestamp = new Timestamp(expected.getLastSwitchingEventTime().getTime());
      assertThat(actual.getLastSwitchingEventTime()).isEqualTo(timestamp);
      assertThat(actual.isLastSwitchingEventState())
          .isEqualTo(expected.isLastSwitchingEventState());
    }
  }

  private void assertLastKnownStateEquals(final RelayStatus expected, final RelayStatus actual) {
    if (expected != null && expected.getLastKnownStateTime() != null) {
      assertThat(actual).isNotNull();

      final Timestamp timestamp = new Timestamp(expected.getLastKnownStateTime().getTime());
      assertThat(actual.getLastKnownStateTime()).isEqualTo(timestamp);
      assertThat(actual.isLastKnownState()).isEqualTo(expected.isLastKnownState());
    }
  }
}
