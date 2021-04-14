/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.entities;

import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import org.apache.commons.lang3.BooleanUtils;
import org.opensmartgridplatform.domain.core.valueobjects.EventType;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

@Entity
public class Event extends AbstractEntity {

  /** Serial Version UID. */
  private static final long serialVersionUID = 5987663923796632312L;

  @Column(nullable = false)
  private String deviceIdentification;

  @Column(nullable = false)
  private Date dateTime;

  @Column(nullable = false, name = "event")
  private EventType eventType;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false, name = "\"index\"")
  private Integer index;

  public Event() {
    // Default constructor
  }

  public Event(
      final String deviceIdentification,
      final Date dateTime,
      final EventType eventType,
      final String description,
      final Integer index) {
    this.deviceIdentification = deviceIdentification;
    this.dateTime = dateTime;
    this.eventType = eventType;
    this.description = description;
    this.index = index;
  }

  public String getDeviceIdentification() {
    return this.deviceIdentification;
  }

  public Date getDateTime() {
    return this.dateTime;
  }

  public EventType getEventType() {
    return this.eventType;
  }

  public String getDescription() {
    return this.description;
  }

  public Integer getIndex() {
    return this.index;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Event)) {
      return false;
    }
    final Event other = (Event) o;

    final boolean[] booleans = new boolean[5];
    booleans[0] = Objects.equals(this.deviceIdentification, other.deviceIdentification);
    booleans[1] = Objects.equals(this.dateTime, other.dateTime);
    booleans[2] = Objects.equals(this.eventType, other.eventType);
    booleans[3] = Objects.equals(this.description, other.description);
    booleans[4] = Objects.equals(this.index, other.index);

    return BooleanUtils.and(booleans);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        this.deviceIdentification, this.dateTime, this.eventType, this.description, this.index);
  }
}
