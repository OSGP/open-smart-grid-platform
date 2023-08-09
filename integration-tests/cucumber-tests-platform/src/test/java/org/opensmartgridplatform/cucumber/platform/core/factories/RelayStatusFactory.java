// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.core.factories;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelayStatusFactory {

  @Autowired private SsldRepository ssldRepository;

  public RelayStatusFactory() {
    // Empty constructor for Spring
  }

  public RelayStatus fromMap(final Ssld ssld, final Map<String, String> settings) {
    final Integer index = getInteger(settings, PlatformKeys.KEY_INDEX);
    final boolean lastSwitchingEventState =
        "On".equals(getString(settings, PlatformKeys.LAST_SWITCHING_EVENT_STATE));
    final ZonedDateTime lastSwitchingEventTime =
        getDate(settings, PlatformKeys.LAST_SWITCHING_EVENT_TIME);
    final boolean lastKnownState = "On".equals(getString(settings, PlatformKeys.LAST_KNOWN_STATE));
    final ZonedDateTime lastKnownStateTime = getDate(settings, PlatformKeys.LAST_KNOWN_STATE_TIME);

    final RelayStatus relayStatus =
        new RelayStatus.Builder(ssld, index)
            .withLastKnownState(lastKnownState, Date.from(lastKnownStateTime.toInstant()))
            .withLastSwitchingEventState(
                lastSwitchingEventState, Date.from(lastSwitchingEventTime.toInstant()))
            .build();

    return relayStatus;
  }

  public RelayStatus fromMap(final Map<String, String> settings) {
    final Integer index = getInteger(settings, PlatformKeys.KEY_INDEX);

    final RelayStatus.Builder builder = new RelayStatus.Builder(index);

    final String eventState = getString(settings, PlatformKeys.LAST_SWITCHING_EVENT_STATE);
    final ZonedDateTime lastSwitchingEventTime =
        getDate(settings, PlatformKeys.LAST_SWITCHING_EVENT_TIME);
    if (eventState != null && lastSwitchingEventTime != null) {
      final boolean lastSwitchingEventState = "On".equals(eventState);
      builder.withLastSwitchingEventState(
          lastSwitchingEventState, Date.from(lastSwitchingEventTime.toInstant()));
    }

    final String knownState = getString(settings, PlatformKeys.LAST_KNOWN_STATE);
    final ZonedDateTime lastKnownStateTime = getDate(settings, PlatformKeys.LAST_KNOWN_STATE_TIME);
    if (knownState != null && lastKnownStateTime != null) {
      final boolean lastKnownState = "On".equals(knownState);
      builder.withLastKnownState(lastKnownState, Date.from(lastKnownStateTime.toInstant()));
    }

    return builder.build();
  }

  public RelayStatus fromDb(final Map<String, String> settings) {
    final String deviceIdentification = getString(settings, PlatformKeys.KEY_DEVICE_IDENTIFICATION);
    final Ssld ssld = this.ssldRepository.findByDeviceIdentification(deviceIdentification);

    final Integer index = getInteger(settings, PlatformKeys.KEY_INDEX);
    return ssld.getRelayStatusByIndex(index);
  }
}
