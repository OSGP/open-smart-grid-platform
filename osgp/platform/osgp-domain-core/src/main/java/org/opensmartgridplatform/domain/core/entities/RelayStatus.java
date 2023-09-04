// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.domain.core.entities;

import java.time.Instant;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import org.opensmartgridplatform.shared.domain.entities.AbstractEntity;

/** An entity class which contains the information of a single relay of a device. */
@Entity
public class RelayStatus extends AbstractEntity {

  private static final long serialVersionUID = -6288672019209482063L;

  @ManyToOne
  @JoinColumn(name = "device_id", nullable = false)
  private Device device;

  @Column private int index;

  @Column private boolean lastSwitchingEventState;

  @Column private Instant lastSwitchingEventTime;

  @Column private boolean lastKnownState;

  @Column private Instant lastKnownStateTime;

  public RelayStatus() {
    // Default constructor for Hibernate
  }

  private RelayStatus(final Builder builder) {
    this.device = builder.device;
    this.index = builder.index;

    this.updateLastKnownState(builder.lastKnownState, builder.lastKnownStateTime);
    this.updateLastSwitchingEventState(
        builder.lastSwitchingEventState, builder.lastSwitchingEventTime);
  }

  public void updateLastSwitchingEventState(final boolean state, final Instant time) {
    this.lastSwitchingEventState = state;
    this.lastSwitchingEventTime = time;

    if (time != null
        && (this.lastKnownStateTime == null || this.lastKnownStateTime.isBefore(time))) {
      this.updateLastKnownState(state, time);
    }
  }

  public void updateLastKnownState(final boolean state, final Instant time) {
    this.lastKnownState = state;
    this.lastKnownStateTime = time;
  }

  public boolean isLastSwitchingEventState() {
    return this.lastSwitchingEventState;
  }

  public Instant getLastSwitchingEventTime() {
    return this.lastSwitchingEventTime;
  }

  public boolean isLastKnownState() {
    return this.lastKnownState;
  }

  public Instant getLastKnownStateTime() {
    return this.lastKnownStateTime;
  }

  public Device getDevice() {
    return this.device;
  }

  public int getIndex() {
    return this.index;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RelayStatus)) {
      return false;
    }
    final RelayStatus relayStatus = (RelayStatus) o;
    final boolean isDeviceEqual = Objects.equals(this.device, relayStatus.device);
    final boolean isIndexEqual = Objects.equals(this.index, relayStatus.index);

    return isDeviceEqual && isIndexEqual;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.device, this.index);
  }

  @Override
  public String toString() {
    return String.format(
        "index: %d, lastSwitchingEventState: %s, lastSwitchingEventTime: %s, lastKnownState: %s, lastKnownStateTime: %s",
        this.index,
        this.lastSwitchingEventState,
        this.lastSwitchingEventTime.toString(),
        this.lastKnownState,
        this.lastKnownStateTime.toString());
  }

  public static class Builder {

    private Device device;
    private final int index;
    private boolean lastSwitchingEventState;
    private Instant lastSwitchingEventTime;
    private boolean lastKnownState;
    private Instant lastKnownStateTime;

    public Builder(final Device device, final int index) {
      this.device = device;
      this.index = index;
    }

    public Builder(final int index) {
      this.index = index;
    }

    public Builder withLastSwitchingEventState(
        final boolean lastSwitchingEventState, final Instant lastSwitchingEventTime) {
      this.lastSwitchingEventState = lastSwitchingEventState;
      this.lastSwitchingEventTime = lastSwitchingEventTime;
      return this;
    }

    public Builder withLastKnownState(
        final boolean lastKnownState, final Instant lastKnownStateTime) {
      this.lastKnownState = lastKnownState;
      this.lastKnownStateTime = lastKnownStateTime;
      return this;
    }

    public RelayStatus build() {
      return new RelayStatus(this);
    }
  }
}
