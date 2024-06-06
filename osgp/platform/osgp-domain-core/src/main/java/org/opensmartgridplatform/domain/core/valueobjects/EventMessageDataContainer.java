// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.valueobjects;

import java.io.Serializable;
import java.util.List;
import org.opensmartgridplatform.domain.core.entities.Event;

public class EventMessageDataContainer implements Serializable {

  /** Serial Version UID. */
  private static final long serialVersionUID = 7539338718527844153L;

  private List<Event> events;

  public EventMessageDataContainer(final List<Event> events) {
    this.events = events;
  }

  public void setEvents(final List<Event> events) {
    this.events = events;
  }

  public List<Event> getEvents() {
    return this.events;
  }
}
