/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class EventMessagesResponse extends ActionResponse implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 3279405192677864979L;

  private List<Event> events;

  public EventMessagesResponse(final List<Event> events) {
    this.events = events;
  }

  public List<Event> getEvents() {
    return this.events;
  }
}
