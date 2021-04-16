/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
