/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.simulator.protocol.iec61850.server;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

import org.openmuc.openiec61850.BasicDataAttribute;
import org.openmuc.openiec61850.BdaBoolean;
import org.openmuc.openiec61850.BdaCheck;
import org.openmuc.openiec61850.BdaDoubleBitPos;
import org.openmuc.openiec61850.BdaEntryTime;
import org.openmuc.openiec61850.BdaFloat32;
import org.openmuc.openiec61850.BdaFloat64;
import org.openmuc.openiec61850.BdaInt16;
import org.openmuc.openiec61850.BdaInt16U;
import org.openmuc.openiec61850.BdaInt32;
import org.openmuc.openiec61850.BdaInt32U;
import org.openmuc.openiec61850.BdaInt64;
import org.openmuc.openiec61850.BdaInt8;
import org.openmuc.openiec61850.BdaInt8U;
import org.openmuc.openiec61850.BdaOctetString;
import org.openmuc.openiec61850.BdaOptFlds;
import org.openmuc.openiec61850.BdaQuality;
import org.openmuc.openiec61850.BdaReasonForInclusion;
import org.openmuc.openiec61850.BdaTapCommand;
import org.openmuc.openiec61850.BdaTimestamp;
import org.openmuc.openiec61850.BdaTriggerConditions;
import org.openmuc.openiec61850.BdaType;
import org.openmuc.openiec61850.BdaUnicodeString;
import org.openmuc.openiec61850.BdaVisibleString;

public class BasicDataAttributeEqualsHelper {

    private static final Map<BdaType, Comparator<BasicDataAttribute>> COMPARATOR_BY_TYPE = new EnumMap<>(
            BdaType.class);

    private static class BdaBooleanComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Boolean.compare(((BdaBoolean) o1).getValue(), ((BdaBoolean) o2).getValue());
        }
    }

    private static class BdaCheckComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaCheck) o1).getValue(),
                    ((BdaCheck) o2).getValue());
        }
    }

    private static class BdaDoubleBitPosComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaDoubleBitPos) o1).getValue(),
                    ((BdaDoubleBitPos) o2).getValue());
        }
    }

    private static class BdaEntryTimeComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Long.compare(((BdaEntryTime) o1).getTimestampValue(), ((BdaEntryTime) o2).getTimestampValue());
        }
    }

    private static class BdaFloat32Comparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return ((BdaFloat32) o1).getFloat().compareTo(((BdaFloat32) o2).getFloat());
        }
    }

    private static class BdaFloat64Comparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return ((BdaFloat64) o1).getDouble().compareTo(((BdaFloat64) o2).getDouble());
        }
    }

    private static class BdaInt16Comparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Short.compare(((BdaInt16) o1).getValue(), ((BdaInt16) o2).getValue());
        }
    }

    private static class BdaInt16UComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Integer.compare(((BdaInt16U) o1).getValue(), ((BdaInt16U) o2).getValue());
        }
    }

    private static class BdaInt32Comparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Integer.compare(((BdaInt32) o1).getValue(), ((BdaInt32) o2).getValue());
        }
    }

    private static class BdaInt32UComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Long.compare(((BdaInt32U) o1).getValue(), ((BdaInt32U) o2).getValue());
        }
    }

    private static class BdaInt64Comparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Long.compare(((BdaInt64) o1).getValue(), ((BdaInt64) o2).getValue());
        }
    }

    private static class BdaInt8Comparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Byte.compare(((BdaInt8) o1).getValue(), ((BdaInt8) o2).getValue());
        }
    }

    private static class BdaInt8UComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return Short.compare(((BdaInt8U) o1).getValue(), ((BdaInt8U) o2).getValue());
        }
    }

    private static class BdaOctetStringComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaOctetString) o1).getValue(),
                    ((BdaOctetString) o2).getValue());
        }
    }

    private static class BdaOptFldsComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaOptFlds) o1).getValue(),
                    ((BdaOptFlds) o2).getValue());
        }
    }

    private static class BdaQualityComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaQuality) o1).getValue(),
                    ((BdaQuality) o2).getValue());
        }
    }

    private static class BdaReasonForInclusionComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaReasonForInclusion) o1).getValue(),
                    ((BdaReasonForInclusion) o2).getValue());
        }
    }

    private static class BdaTapCommandComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return ((BdaTapCommand) o1).getTapCommand().compareTo(((BdaTapCommand) o2).getTapCommand());
        }
    }

    private static class BdaTimestampComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaTimestamp) o1).getValue(),
                    ((BdaTimestamp) o2).getValue());
        }
    }

    private static class BdaTriggerConditionsComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaTriggerConditions) o1).getValue(),
                    ((BdaTriggerConditions) o2).getValue());
        }
    }

    private static class BdaUnicodeStringComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return BasicDataAttributeEqualsHelper.compareByteArray(((BdaUnicodeString) o1).getValue(),
                    ((BdaUnicodeString) o2).getValue());
        }
    }

    private static class BdaVisibleStringComparator implements Comparator<BasicDataAttribute> {
        @Override
        public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
            return ((BdaVisibleString) o1).getStringValue().compareTo(((BdaVisibleString) o2).getStringValue());
        }
    }

    static {
        COMPARATOR_BY_TYPE.put(BdaType.BOOLEAN, new BdaBooleanComparator());
        COMPARATOR_BY_TYPE.put(BdaType.CHECK, new BdaCheckComparator());
        COMPARATOR_BY_TYPE.put(BdaType.DOUBLE_BIT_POS, new BdaDoubleBitPosComparator());
        COMPARATOR_BY_TYPE.put(BdaType.ENTRY_TIME, new BdaEntryTimeComparator());
        COMPARATOR_BY_TYPE.put(BdaType.FLOAT32, new BdaFloat32Comparator());
        COMPARATOR_BY_TYPE.put(BdaType.FLOAT64, new BdaFloat64Comparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT16, new BdaInt16Comparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT16U, new BdaInt16UComparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT32, new BdaInt32Comparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT32U, new BdaInt32UComparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT64, new BdaInt64Comparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT8, new BdaInt8Comparator());
        COMPARATOR_BY_TYPE.put(BdaType.INT8U, new BdaInt8UComparator());
        COMPARATOR_BY_TYPE.put(BdaType.OCTET_STRING, new BdaOctetStringComparator());
        COMPARATOR_BY_TYPE.put(BdaType.OPTFLDS, new BdaOptFldsComparator());
        COMPARATOR_BY_TYPE.put(BdaType.QUALITY, new BdaQualityComparator());
        COMPARATOR_BY_TYPE.put(BdaType.REASON_FOR_INCLUSION, new BdaReasonForInclusionComparator());
        COMPARATOR_BY_TYPE.put(BdaType.TAP_COMMAND, new BdaTapCommandComparator());
        COMPARATOR_BY_TYPE.put(BdaType.TIMESTAMP, new BdaTimestampComparator());
        COMPARATOR_BY_TYPE.put(BdaType.TRIGGER_CONDITIONS, new BdaTriggerConditionsComparator());
        COMPARATOR_BY_TYPE.put(BdaType.UNICODE_STRING, new BdaUnicodeStringComparator());
        COMPARATOR_BY_TYPE.put(BdaType.VISIBLE_STRING, new BdaVisibleStringComparator());
    }

    private BasicDataAttributeEqualsHelper() {
        // Private constructor for utility class.
    }

    private static int compareByteArray(final byte[] a1, final byte[] a2) {
        final int minimumLength = Math.min(a1.length, a2.length);
        for (int i = 0; i < minimumLength; i++) {
            final int compareByte = Byte.compare(a1[i], a2[i]);
            if (compareByte != 0) {
                return compareByte;
            }
        }
        return Integer.compare(a1.length, a2.length);
    }

    public static boolean equals(final BasicDataAttribute bda1, final BasicDataAttribute bda2) {
        if (bda1 == bda2) {
            return true;
        }
        if (bda1 == null || bda2 == null) {
            return false;
        }
        if (bda1.getBasicType() != bda2.getBasicType()) {
            return false;
        }
        return COMPARATOR_BY_TYPE.get(bda1.getBasicType()).compare(bda1, bda2) == 0;
    }
}
