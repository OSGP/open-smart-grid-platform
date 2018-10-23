/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.oslp.elster.infra.networking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for converting sequence number value from Integer to byte array
 * and vice versa.
 */
public class SequenceNumberUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceNumberUtils.class);
    private static final boolean IS_LOGGING_ENABLED = false;

    /**
     * Private constructor for utility class.
     */
    private SequenceNumberUtils() {
        // Nothing needed here.
    }

    /**
     * Convert an Integer object's value into a byte array of size 2. The range
     * of the value can be from 0 to 65535, equal to a 16 bit unsigned value.
     *
     * @param value
     *            The integer value to convert into the byte array.
     *
     * @return Byte array of size 2.
     */
    public static byte[] convertIntegerToByteArray(final Integer value) {
        final byte[] bytes = new byte[2];
        bytes[0] = (byte) (value >>> 8);
        bytes[1] = value.byteValue();
        if (IS_LOGGING_ENABLED) {
            LOGGER.info(
                    "platform.SequenceNumberUtils.convertIntegerToByteArray() byte[0]: {} byte[1]: {} Integer value: {}",
                    bytes[0], bytes[1], value);
        }
        return bytes;
    }

    /**
     * Convert a byte array of size 2 into an Integer object. The range of the
     * value can be from 0 to 65535, equal to a 16 bit unsigned value.
     *
     * @param array
     *            The byte array of size 2 to convert into an Integer object.
     *
     * @return The Integer object.
     */
    public static Integer convertByteArrayToInteger(final byte[] array) {
        final Integer value = (array[0] & 0xFF) << 8 | (array[1] & 0xFF);
        if (IS_LOGGING_ENABLED) {
            LOGGER.info(
                    "platform.SequenceNumberUtils.convertByteArrayToInteger() byte[0]: {} byte[1]: {} Integer value: {}",
                    array[0], array[1], value);
        }
        return value;
    }
}
