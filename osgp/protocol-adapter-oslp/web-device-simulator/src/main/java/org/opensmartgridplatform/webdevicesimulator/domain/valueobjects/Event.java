// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.webdevicesimulator.domain.valueobjects;

import org.opensmartgridplatform.oslp.Oslp;

public class Event {

  private final Oslp.Event oslpEvent;
  private final String description;
  private final Integer index;
  private final String timestamp;
  private final boolean hasTimestamp;

  public Event(
      final Oslp.Event oslpEvent,
      final String description,
      final Integer index,
      final String timestamp,
      final boolean hasTimestamp) {
    this.oslpEvent = oslpEvent;
    this.description = description;
    this.index = index;
    this.timestamp = timestamp;
    this.hasTimestamp = hasTimestamp;
  }

  public Oslp.Event getOslpEvent() {
    return this.oslpEvent;
  }

  public String getDescription() {
    return this.description;
  }

  public Integer getIndex() {
    return this.index;
  }

  public String getTimestamp() {
    return this.timestamp;
  }

  public boolean hasTimestamp() {
    return this.hasTimestamp;
  }
}
