/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.platform.core.factories;

import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getDate;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getInteger;
import static org.opensmartgridplatform.cucumber.core.ReadSettingsHelper.getString;

import java.util.Map;

import org.joda.time.DateTime;
import org.opensmartgridplatform.cucumber.platform.PlatformKeys;
import org.opensmartgridplatform.domain.core.entities.RelayStatus;
import org.opensmartgridplatform.domain.core.entities.Ssld;
import org.opensmartgridplatform.domain.core.repositories.SsldRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RelayStatusFactory {

    @Autowired
    private SsldRepository ssldRepository;

    public RelayStatusFactory() {
        // Empty constructor for Spring
    }

    public RelayStatus fromMap(final Ssld ssld, final Map<String, String> settings) {
        final Integer index = getInteger(settings, PlatformKeys.KEY_INDEX);
        final boolean lastSwitchingEventState = "On"
                .equals(getString(settings, PlatformKeys.LAST_SWITCHING_EVENT_STATE));
        final DateTime lastSwitchingEventTime = getDate(settings, PlatformKeys.LAST_SWITCHING_EVENT_TIME);
        final boolean lastKnownState = "On".equals(getString(settings, PlatformKeys.LAST_KNOWN_STATE));
        final DateTime lastKnownStateTime = getDate(settings, PlatformKeys.LAST_KNOWN_STATE_TIME);

        final RelayStatus relayStatus = new RelayStatus.Builder(ssld, index)
                .withLastKnownState(lastKnownState, lastKnownStateTime.toDate())
                .withLastSwitchingEventState(lastSwitchingEventState, lastSwitchingEventTime.toDate()).build();

        return relayStatus;
    }

    public RelayStatus fromMap(final Map<String, String> settings) {
        final Integer index = getInteger(settings, PlatformKeys.KEY_INDEX);

        final RelayStatus.Builder builder = new RelayStatus.Builder(index);

        final String eventState = getString(settings, PlatformKeys.LAST_SWITCHING_EVENT_STATE);
        final DateTime lastSwitchingEventTime = getDate(settings, PlatformKeys.LAST_SWITCHING_EVENT_TIME);
        if (eventState != null && lastSwitchingEventTime != null) {
            final boolean lastSwitchingEventState = "On".equals(eventState);
            builder.withLastSwitchingEventState(lastSwitchingEventState, lastSwitchingEventTime.toDate());
        }

        final String knownState = getString(settings, PlatformKeys.LAST_KNOWN_STATE);
        final DateTime lastKnownStateTime = getDate(settings, PlatformKeys.LAST_KNOWN_STATE_TIME);
        if (knownState != null && lastKnownStateTime != null) {
            final boolean lastKnownState = "On".equals(knownState);
            builder.withLastKnownState(lastKnownState, lastKnownStateTime.toDate());
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
