// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.domain.valueobjects;

import org.joda.time.DateTime;

/** Value object, containing all data that is written to a relay schedule */
public class ScheduleEntry {

  private final boolean enabled;
  private final TriggerType triggerType;
  private final int day;
  private final short time;
  private final boolean on;
  private final int minimumLightsOn;
  private final int triggerWindowMinutesBefore;
  private final int triggerWindowMinutesAfter;

  public ScheduleEntry(final Builder builder) {
    this.enabled = builder.enabled;
    this.triggerType = builder.triggerType;
    this.day = builder.day;
    this.time = builder.time;
    this.on = builder.on;
    this.minimumLightsOn = builder.minimumLightsOn;
    this.triggerWindowMinutesBefore = builder.triggerWindowMinutesBefore;
    this.triggerWindowMinutesAfter = builder.triggerWindowMinutesAfter;
  }

  public static final class Builder {

    private static final boolean DEFAULT_ENABLED = true;
    private static final int DEFAULT_MINIMUM_LIGHTS_ON = 30;
    private static final int DEFAULT_WINDOW_MINUTES = 30;

    private boolean enabled = DEFAULT_ENABLED;
    private TriggerType triggerType = null;
    private Integer day = null;
    private Short time = null;
    private Boolean on = null;
    private int minimumLightsOn = DEFAULT_MINIMUM_LIGHTS_ON;
    private int triggerWindowMinutesBefore = DEFAULT_WINDOW_MINUTES;
    private int triggerWindowMinutesAfter = DEFAULT_WINDOW_MINUTES;

    public Builder enabled(final boolean enabled) {
      this.enabled = enabled;
      return this;
    }

    public Builder triggerType(final TriggerType triggerType) {
      this.triggerType = triggerType;
      return this;
    }

    /**
     * Makes this {@link ScheduleEntry} work on the {@link ScheduleWeekday} provided. To create an
     * entry for special days, use {@link #specialDay(DateTime)}.
     *
     * @return this builder.
     */
    public Builder weekday(final ScheduleWeekday weekday) {
      this.day = weekday.getIndex();
      return this;
    }

    /**
     * Makes this {@link ScheduleEntry} work on the {@link DateTime} provided as a special day. To
     * create an entry for a certain day of the week, for weekdays, or for weekend days use {@link
     * #weekday(ScheduleWeekday)}.
     *
     * @return this builder.
     */
    public Builder specialDay(final DateTime specialDate) {
      // make weekday the int value corresponding with yyyyMMdd
      this.day =
          specialDate.getDayOfMonth()
              + 100 * specialDate.getMonthOfYear()
              + 10000 * specialDate.getYear();
      return this;
    }

    /**
     * Makes this {@link ScheduleEntry} work on the {@code time} provided as {@code short} value
     * (e.g. 06:35 is represented by 635).
     *
     * @param time a time formatted as hhmm, interpreted as {@code short}.
     * @return this builder.
     * @throws IllegalArgumentException if {@code time} is a value that does not match a time value
     *     as described.
     */
    public Builder time(final short time) {
      if (time < 0 || time > 2359) {
        throw new IllegalArgumentException("time value must be within [0..2359]: " + time);
      }
      if (time % 100 > 59) {
        throw new IllegalArgumentException(
            "time value must not end with two digits outside [0..59]: " + time);
      }
      this.time = time;
      return this;
    }

    public Builder on(final boolean on) {
      this.on = on;
      return this;
    }

    public Builder triggerWindowMinutesBefore(final int triggerWindowMinutesBefore) {
      if (triggerWindowMinutesBefore < 0) {
        throw new IllegalArgumentException(
            "triggerWindowMinutesBefore must be non-negative: " + triggerWindowMinutesBefore);
      }
      this.triggerWindowMinutesBefore = triggerWindowMinutesBefore;
      return this;
    }

    public Builder triggerWindowMinutesAfter(final int triggerWindowMinutesAfter) {
      if (triggerWindowMinutesAfter < 0) {
        throw new IllegalArgumentException(
            "triggerWindowMinutesAfter must be non-negative: " + triggerWindowMinutesAfter);
      }
      this.triggerWindowMinutesAfter = triggerWindowMinutesAfter;
      return this;
    }

    public Builder minimumLightsOn(final int minimumLightsOn) {
      if (minimumLightsOn < 0) {
        throw new IllegalArgumentException(
            "minimumLightsOn must be non-negative: " + minimumLightsOn);
      }
      this.minimumLightsOn = minimumLightsOn;
      return this;
    }

    /**
     * Constructs a {@link ScheduleEntry} for the given inputs, where enabled (default {@value
     * #DEFAULT_ENABLED}), minimumLightsOn (default {@value #DEFAULT_MINIMUM_LIGHTS_ON}),
     * triggerWindowMinutesBefore (default {@value #DEFAULT_WINDOW_MINUTES}) and ,
     * triggerWindowMinutesAfter (default {@value #DEFAULT_WINDOW_MINUTES}) do not need to have been
     * set explicitly when they should have their default values.
     *
     * @return a {@link ScheduleEntry} for the given inputs.
     * @throws IllegalStateException if triggerType, day (weekday or special day), time (for fixed
     *     time entries) or on/off has not been set.
     */
    public ScheduleEntry build() {
      if (this.triggerType == null) {
        throw new IllegalStateException("A ScheduleEntry can only be created with a triggerType.");
      }
      if (this.day == null) {
        throw new IllegalStateException(
            "A ScheduleEntry can only be created with either a weekday or special day.");
      }
      if (this.time == null) {
        if (TriggerType.FIX.equals(this.triggerType)) {
          throw new IllegalStateException(
              "A ScheduleEntry for fixed time can only be created with a time.");
        } else {
          /*
           * Use a default value 0 for time, when triggered by sensor or astronomical
           * time.
           */
          this.time = 0;
        }
      }
      if (this.on == null) {
        throw new IllegalStateException(
            "A ScheduleEntry can only be created with on/off switching.");
      }
      return new ScheduleEntry(this);
    }

    public static Builder newBuilder() {
      return new Builder();
    }
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public TriggerType getTriggerType() {
    return this.triggerType;
  }

  public int getDay() {
    return this.day;
  }

  public short getTime() {
    return this.time;
  }

  public boolean isOn() {
    return this.on;
  }

  public int getMinimumLightsOn() {
    return this.minimumLightsOn;
  }

  public int getTriggerWindowMinutesBefore() {
    return this.triggerWindowMinutesBefore;
  }

  public int getTriggerWindowMinutesAfter() {
    return this.triggerWindowMinutesAfter;
  }
}
