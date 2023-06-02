//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class ControlledClock extends Clock {

  private final ZoneId zone;
  private final AtomicLong millis;

  public ControlledClock() {
    this(ZoneOffset.UTC, 0);
  }

  public ControlledClock(final ZoneId zone, final long millis) {
    this.zone = Objects.requireNonNull(zone, "zone");
    if (millis < 0) {
      throw new IllegalArgumentException(
          "millis must be non-negative, this clock works starting from the epoch: " + millis);
    }
    this.millis = new AtomicLong(millis);
  }

  @Override
  public ZoneId getZone() {
    return this.zone;
  }

  @Override
  public Clock withZone(final ZoneId zone) {
    return new ControlledClock(zone, this.millis.get());
  }

  /**
   * Returns a copy of this clock with the provided time in milliseconds.
   *
   * @param millis the number of milliseconds from the epoch of 1970-01-01T00:00:00Z that will be
   *     the time of the returned clock
   * @return a new clock based on this clock with the given number of milliseconds as its time
   */
  public Clock withMillis(final long millis) {
    return new ControlledClock(this.zone, millis);
  }

  @Override
  public Instant instant() {
    return Instant.ofEpochMilli(this.millis());
  }

  @Override
  public long millis() {
    return this.millis.get();
  }

  /**
   * Advances the time of this clock with the provided number of milliseconds.
   *
   * @param millis the number of milliseconds to advance this clock by, non-negative
   * @return the number of milliseconds from the epoch of 1970-01-01T00:00:00Z that represents the
   *     new time of this clock
   */
  public long advanceMillis(final long millis) {
    if (millis < 0) {
      throw new IllegalArgumentException(
          "millis must be non-negative, this clock does not go back in time: " + millis);
    }
    final long newMillis = this.millis.addAndGet(millis);
    if (newMillis < 0) {
      this.millis.set(0);
      throw new IllegalStateException("Clock ran out of time, reset to epoch: " + newMillis);
    }
    return newMillis;
  }

  /**
   * Advances the time of this clock with one millisecond.
   *
   * @return the number of milliseconds from the epoch of 1970-01-01T00:00:00Z that represents the
   *     new time of this clock
   */
  public long tick() {
    return this.advanceMillis(1);
  }
}
