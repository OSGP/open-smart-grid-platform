/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.openmuc.jdlms.interfaceclass.attribute.MbusClientAttribute;

/**
 * Represents the M-Bus Client Setup identification number.
 * <p>
 * The IdentificationNumber in its textual form consists of the last 8 digits of
 * the identification number.<br>
 * The long value is the base-10 value calculated from the textual
 * representation interpreted as hexadecimal (base-16). This value is the one
 * that is used for the indentification_number (attribute 6) of the DLMS M-Bus
 * client (class ID 72).
 *
 * @see MbusClientAttribute#IDENTIFICATION_NUMBER
 */
public class IdentificationNumber {

    private static final int HEX_RADIX = 16;
    private static final String IDENTIFICATION_NUMBER_REGEX = "\\d{1,8}";

    private static final long MIN_IDENTIFICATION = 0;
    private static final long MAX_IDENTIFICATION = Long.parseLong("99999999", HEX_RADIX);

    private final String last8Digits;

    private IdentificationNumber(final String last8Digits) {
        validateLast8Digits(last8Digits);
        if (StringUtils.isBlank(last8Digits)) {
            this.last8Digits = null;
        } else {
            /*
             * If a String of less than 8 digits is given, make sure it is
             * prefixed with zero digits up to a length of 8.
             */
            this.last8Digits = String.format("%08d", Integer.valueOf(last8Digits));
        }
    }

    public static IdentificationNumber fromIdentification(final Long identification) {
        final String last8Digits = calculateLast8Digits(identification);
        try {
            validateLast8Digits(last8Digits);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "identification must represent a BCD (HEX value consisting of digits only): " + identification, e);
        }
        return new IdentificationNumber(last8Digits);
    }

    public static IdentificationNumber fromLast8Digits(final String last8Digits) {
        return new IdentificationNumber(last8Digits);
    }

    public static IdentificationNumber fromDataObject(final DataObject dataObject) {
        final long identification;
        if (dataObject == null || dataObject.isNull()) {
            return new IdentificationNumber(null);
        } else if (DataObject.Type.DOUBLE_LONG_UNSIGNED != dataObject.getType()) {
            throw new IllegalArgumentException("dataObject type must be double-long-unsigned: " + dataObject.getType());
        } else {
            identification = ((Number) dataObject.getRawValue()).longValue();
        }
        return fromIdentification(identification);
    }

    private static Long calculateIdentification(final String last8Digits) {
        if (StringUtils.isBlank(last8Digits)) {
            return null;
        }
        validateLast8Digits(last8Digits);
        return Long.parseLong(last8Digits, HEX_RADIX);
    }

    private static void validateLast8Digits(final String last8Digits) {
        if (StringUtils.isNotBlank(last8Digits) && !last8Digits.matches(IDENTIFICATION_NUMBER_REGEX)) {
            throw new IllegalArgumentException("last8Digits must be up to 8 digits: \"" + last8Digits + "\"");
        }
    }

    private static String calculateLast8Digits(final Long identification) {
        if (identification == null) {
            return null;
        }
        if (identification < MIN_IDENTIFICATION || identification > MAX_IDENTIFICATION) {
            throw new IllegalArgumentException("identification not in [" + MIN_IDENTIFICATION + ".."
                    + MAX_IDENTIFICATION + "]: " + identification);
        }
        return String.format("%08X", identification);
    }

    @Override
    public String toString() {
        return String.format("IdentificationNumber[%s(%d)]", this.last8Digits,
                calculateIdentification(this.last8Digits));
    }

    /**
     *
     * @return a DataObject with the double-long-unsigned value of the
     *         identification number
     */
    public DataObject asDataObject() {
        if (StringUtils.isBlank(this.last8Digits)) {
            return DataObject.newNullData();
        }
        return DataObject.newUInteger32Data(calculateIdentification(this.last8Digits));
    }

    public Long getIdentificationNumber() {
        return calculateIdentification(this.last8Digits);
    }

    public String getLast8Digits() {
        return this.last8Digits;
    }

    public Integer getLast8DigitsAsInteger() {
        if (StringUtils.isBlank(this.last8Digits)) {
            return null;
        }
        return Integer.valueOf(this.last8Digits);
    }
}
