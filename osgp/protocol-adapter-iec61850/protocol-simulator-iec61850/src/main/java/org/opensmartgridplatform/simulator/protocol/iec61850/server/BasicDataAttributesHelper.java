// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.simulator.protocol.iec61850.server;

import com.beanit.openiec61850.BasicDataAttribute;
import com.beanit.openiec61850.BdaBoolean;
import com.beanit.openiec61850.BdaCheck;
import com.beanit.openiec61850.BdaDoubleBitPos;
import com.beanit.openiec61850.BdaEntryTime;
import com.beanit.openiec61850.BdaFloat32;
import com.beanit.openiec61850.BdaFloat64;
import com.beanit.openiec61850.BdaInt16;
import com.beanit.openiec61850.BdaInt16U;
import com.beanit.openiec61850.BdaInt32;
import com.beanit.openiec61850.BdaInt32U;
import com.beanit.openiec61850.BdaInt64;
import com.beanit.openiec61850.BdaInt8;
import com.beanit.openiec61850.BdaInt8U;
import com.beanit.openiec61850.BdaOctetString;
import com.beanit.openiec61850.BdaOptFlds;
import com.beanit.openiec61850.BdaQuality;
import com.beanit.openiec61850.BdaReasonForInclusion;
import com.beanit.openiec61850.BdaTapCommand;
import com.beanit.openiec61850.BdaTapCommand.TapCommand;
import com.beanit.openiec61850.BdaTimestamp;
import com.beanit.openiec61850.BdaTriggerConditions;
import com.beanit.openiec61850.BdaType;
import com.beanit.openiec61850.BdaUnicodeString;
import com.beanit.openiec61850.BdaVisibleString;
import com.beanit.openiec61850.HexConverter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.EnumMap;
import java.util.Map;
import java.util.TimeZone;
import org.springframework.util.StringUtils;

public class BasicDataAttributesHelper {

  private static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

  static {
    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
  }

  private static interface ValueSetter {
    void setValue(BasicDataAttribute bda, String value);
  }

  private static final Map<BdaType, ValueSetter> VALUE_SETTER_BY_TYPE =
      new EnumMap<>(BdaType.class);

  private static final Map<BdaType, Comparator<BasicDataAttribute>> COMPARATOR_BY_TYPE =
      new EnumMap<>(BdaType.class);

  private static class BdaBooleanComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return Boolean.compare(((BdaBoolean) o1).getValue(), ((BdaBoolean) o2).getValue());
    }
  }

  private static class BdaCheckComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaCheck) o1).getValue(), ((BdaCheck) o2).getValue());
    }
  }

  private static class BdaDoubleBitPosComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaDoubleBitPos) o1).getValue(), ((BdaDoubleBitPos) o2).getValue());
    }
  }

  private static class BdaEntryTimeComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return Long.compare(
          ((BdaEntryTime) o1).getTimestampValue(), ((BdaEntryTime) o2).getTimestampValue());
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
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaOctetString) o1).getValue(), ((BdaOctetString) o2).getValue());
    }
  }

  private static class BdaOptFldsComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaOptFlds) o1).getValue(), ((BdaOptFlds) o2).getValue());
    }
  }

  private static class BdaQualityComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaQuality) o1).getValue(), ((BdaQuality) o2).getValue());
    }
  }

  private static class BdaReasonForInclusionComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaReasonForInclusion) o1).getValue(), ((BdaReasonForInclusion) o2).getValue());
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
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaTimestamp) o1).getValue(), ((BdaTimestamp) o2).getValue());
    }
  }

  private static class BdaTriggerConditionsComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaTriggerConditions) o1).getValue(), ((BdaTriggerConditions) o2).getValue());
    }
  }

  private static class BdaUnicodeStringComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return BasicDataAttributesHelper.compareByteArray(
          ((BdaUnicodeString) o1).getValue(), ((BdaUnicodeString) o2).getValue());
    }
  }

  private static class BdaVisibleStringComparator implements Comparator<BasicDataAttribute> {
    @Override
    public int compare(final BasicDataAttribute o1, final BasicDataAttribute o2) {
      return ((BdaVisibleString) o1)
          .getStringValue()
          .compareTo(((BdaVisibleString) o2).getStringValue());
    }
  }

  private static class BdaBooleanValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaBoolean) bda).setValue(Boolean.parseBoolean(value));
    }
  }

  private static class BdaCheckValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaCheck) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaDoubleBitPosValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaDoubleBitPos) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaEntryTimeValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaEntryTime) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaFloat32ValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaFloat32) bda).setFloat(Float.valueOf(value));
    }
  }

  private static class BdaFloat64ValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaFloat64) bda).setDouble(Double.valueOf(value));
    }
  }

  private static class BdaInt16ValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt16) bda).setValue(Short.parseShort(value));
    }
  }

  private static class BdaInt16UValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt16U) bda).setValue(Integer.parseInt(value));
    }
  }

  private static class BdaInt32ValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt32) bda).setValue(Integer.parseInt(value));
    }
  }

  private static class BdaInt32UValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt32U) bda).setValue(Long.parseLong(value));
    }
  }

  private static class BdaInt64ValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt64) bda).setValue(Long.parseLong(value));
    }
  }

  private static class BdaInt8ValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt8) bda).setValue(Byte.parseByte(value));
    }
  }

  private static class BdaInt8UValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaInt8U) bda).setValue(Short.parseShort(value));
    }
  }

  private static class BdaOctetStringValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaOctetString) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaOptFldsValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaOptFlds) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaQualityValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaQuality) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaReasonForInclusionValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaReasonForInclusion) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaTapCommandValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaTapCommand) bda).setTapCommand(TapCommand.valueOf(value));
    }
  }

  private static class BdaTimestampValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaTimestamp) bda).setDate(BasicDataAttributesHelper.parseDate(value));
    }
  }

  private static class BdaTriggerConditionsValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaTriggerConditions) bda).setValue(BasicDataAttributesHelper.toByteArray(value));
    }
  }

  private static class BdaUnicodeStringValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaUnicodeString) bda).setValue(value.getBytes(StandardCharsets.UTF_8));
    }
  }

  private static class BdaVisibleStringValueSetter implements ValueSetter {
    @Override
    public void setValue(final BasicDataAttribute bda, final String value) {
      ((BdaVisibleString) bda).setValue(value.getBytes(StandardCharsets.US_ASCII));
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

    VALUE_SETTER_BY_TYPE.put(BdaType.BOOLEAN, new BdaBooleanValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.CHECK, new BdaCheckValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.DOUBLE_BIT_POS, new BdaDoubleBitPosValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.ENTRY_TIME, new BdaEntryTimeValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.FLOAT32, new BdaFloat32ValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.FLOAT64, new BdaFloat64ValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT16, new BdaInt16ValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT16U, new BdaInt16UValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT32, new BdaInt32ValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT32U, new BdaInt32UValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT64, new BdaInt64ValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT8, new BdaInt8ValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.INT8U, new BdaInt8UValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.OCTET_STRING, new BdaOctetStringValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.OPTFLDS, new BdaOptFldsValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.QUALITY, new BdaQualityValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.REASON_FOR_INCLUSION, new BdaReasonForInclusionValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.TAP_COMMAND, new BdaTapCommandValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.TIMESTAMP, new BdaTimestampValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.TRIGGER_CONDITIONS, new BdaTriggerConditionsValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.UNICODE_STRING, new BdaUnicodeStringValueSetter());
    VALUE_SETTER_BY_TYPE.put(BdaType.VISIBLE_STRING, new BdaVisibleStringValueSetter());
  }

  private BasicDataAttributesHelper() {
    // Private constructor for utility class.
  }

  public static Date parseDate(final String date) {
    if (StringUtils.isEmpty(date)) {
      return null;
    }
    synchronized (DATE_FORMAT) {
      try {
        return DATE_FORMAT.parse(date);
      } catch (final ParseException e) {
        throw new AssertionError(
            "Input \"" + date + "\" cannot be parsed with pattern \"" + DATE_FORMAT_PATTERN + "\"",
            e);
      }
    }
  }

  private static byte[] toByteArray(final String hexString) {
    if ("".equals(hexString)) {
      return new byte[0];
    }
    return HexConverter.fromShortHexString(hexString);
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

  public static boolean attributesEqual(
      final BasicDataAttribute bda1, final BasicDataAttribute bda2) {
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

  public static void setValue(final BasicDataAttribute bda, final String value) {
    VALUE_SETTER_BY_TYPE.get(bda.getBasicType()).setValue(bda, value);
  }
}
