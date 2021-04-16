/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
