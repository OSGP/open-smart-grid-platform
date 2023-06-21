// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.database.core;

import org.opensmartgridplatform.domain.core.entities.Event;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecifications {

  private static final String DEVICE_IDENTIFICATION = "deviceIdentification";
  private static final String EVENT_TYPE = "eventType";

  public static Specification<Event> isFromDevice(final String deviceIdentification) {
    return ((eventRoot, query, cb) ->
        cb.equal(eventRoot.<String>get(DEVICE_IDENTIFICATION), deviceIdentification));
  }

  public static Specification<Event> hasEventType(final EventType eventType) {

    return ((eventRoot, query, cb) -> cb.equal(eventRoot.<Event>get(EVENT_TYPE), eventType));
  }
}
