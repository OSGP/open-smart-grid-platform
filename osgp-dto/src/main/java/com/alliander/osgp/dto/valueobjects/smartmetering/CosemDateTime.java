/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

public class CosemDateTime implements Serializable {

    private static final long serialVersionUID = 4157582293990514746L;

    private static final int MILLISECONDS_PER_MINUTE = 60 * 1000;

    public static final int DEVIATION_NOT_SPECIFIED = 0x8000;

    private final CosemDate date;
    private final CosemTime time;

    private final int deviation;

    private final ClockStatus clockStatus;

    public CosemDateTime(final CosemDate date, final CosemTime time, final int deviation, final ClockStatus clockStatus) {
        Objects.requireNonNull(date, "date must not be null");
        Objects.requireNonNull(time, "time must not be null");
        Objects.requireNonNull(clockStatus, "clockStatus must not be null");
        this.checkDeviation(deviation);
        this.date = date;
        this.time = time;
        if (deviation == -DEVIATION_NOT_SPECIFIED) {
            /*
             * Has to do with specifics regarding 4 byte shorts and int values.
             * See comments with isDeviationNotSpecified(int).
             */
            this.deviation = DEVIATION_NOT_SPECIFIED;
        } else {
            this.deviation = deviation;
        }
        this.clockStatus = clockStatus;
    }

    private void checkDeviation(final int deviation) {
        if (!this.isValidDeviation(deviation)) {
            throw new IllegalArgumentException("Deviation not in [-720..720, 0x8000]: " + deviation);
        }
    }

    private boolean isValidDeviation(final int deviation) {
        return this.isSpecificDeviation(deviation) || this.isDeviationNotSpecified(deviation);
    }

    private boolean isSpecificDeviation(final int deviation) {
        return deviation >= -720 && deviation <= 720;
    }

    private boolean isDeviationNotSpecified(final int deviation) {
        /*
         * Deviation comes from a short value (4 bytes), where the int value of
         * DEVIATION_NOT_SPECIFIED equals the negative value as 4 byte short.
         * Take this into account checking the deviation value.
         */
        return DEVIATION_NOT_SPECIFIED == deviation || -DEVIATION_NOT_SPECIFIED == deviation;
    }

    public CosemDateTime(final LocalDate date, final LocalTime time, final int deviation, final ClockStatus clockStatus) {
        this(new CosemDate(date), new CosemTime(time), deviation, clockStatus);
    }

    public CosemDateTime(final LocalDate date, final LocalTime time, final int deviation) {
        this(new CosemDate(date), new CosemTime(time), deviation, new ClockStatus(ClockStatus.STATUS_NOT_SPECIFIED));
    }

    public CosemDateTime(final LocalDateTime dateTime, final int deviation, final ClockStatus clockStatus) {
        this(dateTime.toLocalDate(), dateTime.toLocalTime(), deviation, clockStatus);
    }

    public CosemDateTime(final LocalDateTime dateTime, final int deviation) {
        this(dateTime.toLocalDate(), dateTime.toLocalTime(), deviation);
    }

    public CosemDateTime(final DateTime dateTime) {
        this(dateTime.toLocalDate(), dateTime.toLocalTime(), determineDeviation(dateTime),
                determineClockStatus(dateTime));
    }

    private static int determineDeviation(final DateTime dateTime) {
        return -(dateTime.getZone().getOffset(dateTime.getMillis()) / MILLISECONDS_PER_MINUTE);
    }

    private static ClockStatus determineClockStatus(final DateTime dateTime) {
        final Set<ClockStatusBit> statusBits = EnumSet.noneOf(ClockStatusBit.class);
        if (!dateTime.getZone().isStandardOffset(dateTime.getMillis())) {
            statusBits.add(ClockStatusBit.DAYLIGHT_SAVING_ACTIVE);
        }
        return new ClockStatus(statusBits);
    }

    public CosemDateTime() {
        this(DateTime.now());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CosemDateTime[");
        sb.append(this.date);
        sb.append(' ');
        sb.append(this.time);
        sb.append(", deviation=" + this.deviation);
        sb.append(", " + this.clockStatus);
        return sb.append(']').toString();
    }

    public byte[] toByteArray() {
        final byte[] data = new byte[12];

        data[0] = (byte) (this.date.getYear() & 0xFF);
        data[1] = (byte) ((this.date.getYear() >> 8) & 0xFF);
        data[2] = (byte) this.date.getMonth();
        data[3] = (byte) this.date.getDayOfMonth();
        data[4] = (byte) this.date.getDayOfWeek();
        data[5] = (byte) this.time.getHour();
        data[6] = (byte) this.time.getMinute();
        data[7] = (byte) this.time.getSecond();
        data[8] = (byte) this.time.getHundredths();
        data[9] = (byte) (this.getDeviation() & 0xFF);
        data[10] = (byte) ((this.getDeviation() >> 8) & 0xFF);
        data[11] = (byte) this.getClockStatus().getStatus();

        return data;
    }

    public CosemDate getDate() {
        return this.date;
    }

    public CosemTime getTime() {
        return this.time;
    }

    public int getDeviation() {
        return this.deviation;
    }

    public ClockStatus getClockStatus() {
        return this.clockStatus;
    }

    public boolean isDeviationSpecified() {
        return DEVIATION_NOT_SPECIFIED != this.deviation;
    }

    /**
     * @return {@code true} if the date, time and deviation are specified;
     *         {@code false} otherwise.
     *
     * @see #isLocalDateTimeSpecified()
     * @see #isDeviationSpecified()
     */
    public boolean isDateTimeSpecified() {
        return this.isLocalDateTimeSpecified() && this.isDeviationSpecified();
    }

    /**
     * Returns this {@link CosemDateTime} as {@link DateTime} if the date, time
     * and deviation are specified.
     *
     * @return this {@link CosemDateTime} as {@link DateTime}, or {@code null}
     *         if not {@link #isDateTimeSpecified()}.
     *
     * @see #isDateTimeSpecified()
     */
    public DateTime asDateTime() {
        if (!this.isDateTimeSpecified()) {
            return null;
        }
        final LocalDateTime localDateTime = this.asLocalDateTime();
        final DateTimeZone zone = DateTimeZone.forOffsetMillis(-this.deviation * MILLISECONDS_PER_MINUTE);
        return localDateTime.toDateTime(zone);
    }

    /**
     * @return {@code true} if the date and time are specified; {@code false}
     *         otherwise.
     *
     * @see #isLocalDateSpecified()
     * @see #isLocalTimeSpecified()
     */
    public boolean isLocalDateTimeSpecified() {
        return this.date.isLocalDateSpecified() && this.time.isLocalTimeSpecified();
    }

    /**
     * Returns this {@link CosemDateTime} as {@link LocalDateTime} if the date
     * and time are specified.
     *
     * @return this {@link CosemDateTime} as {@link LocalDateTime}, or
     *         {@code null} if not {@link #isLocalDateTimeSpecified()}.
     *
     * @see #isLocalDateTimeSpecified()
     */
    public LocalDateTime asLocalDateTime() {
        if (!this.isLocalDateTimeSpecified()) {
            return null;
        }
        if (this.time.isSecondNotSpecified()) {
            return new LocalDateTime(this.date.getYear(), this.date.getMonth(), this.date.getDayOfMonth(),
                    this.time.getHour(), this.time.getMinute());
        }
        if (this.time.isHundredthsNotSpecified()) {
            return new LocalDateTime(this.date.getYear(), this.date.getMonth(), this.date.getDayOfMonth(),
                    this.time.getHour(), this.time.getMinute(), this.time.getSecond());
        }
        return new LocalDateTime(this.date.getYear(), this.date.getMonth(), this.date.getDayOfMonth(),
                this.time.getHour(), this.time.getMinute(), this.time.getSecond(), this.time.getHundredths() * 10);
    }

    /**
     * @return {@code true} if the date is specified; {@code false} otherwise.
     *
     * @see #getDate()
     * @see CosemDate#isLocalDateSpecified()
     */
    public boolean isLocalDateSpecified() {
        return this.date.isLocalDateSpecified();
    }

    /**
     * Returns this {@link CosemDateTime} as {@link LocalDate} if the date is
     * specified.
     *
     * @return this {@link CosemDateTime} as {@link LocalDate}, or {@code null}
     *         if not {@link #isLocalDateSpecified()}.
     *
     * @see #isLocalDateSpecified()
     */
    public LocalDate asLocalDate() {
        return this.date.asLocalDate();
    }

    /**
     * @return {@code true} if the time is specified; {@code false} otherwise.
     *
     * @see #getTime()
     * @see CosemTime#isLocalTimeSpecified()
     */
    public boolean isLocalTimeSpecified() {
        return this.time.isLocalTimeSpecified();
    }

    /**
     * Returns this {@link CosemDateTime} as {@link LocalTime} if the time is
     * specified.
     *
     * @return this {@link CosemDateTime} as {@link LocalTime}, or {@code null}
     *         if not {@link #isLocalTimeSpecified()}.
     *
     * @see #isLocalTimeSpecified()
     */
    public LocalTime asLocalTime() {
        return this.time.asLocalTime();
    }
}
