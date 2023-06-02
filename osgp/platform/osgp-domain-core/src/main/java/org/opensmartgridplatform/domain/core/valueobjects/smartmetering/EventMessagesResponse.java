//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
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
