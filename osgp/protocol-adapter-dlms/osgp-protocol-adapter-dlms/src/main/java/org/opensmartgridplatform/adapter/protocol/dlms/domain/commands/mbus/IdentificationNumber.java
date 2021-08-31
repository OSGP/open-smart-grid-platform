/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;

/**
 * Represents the M-Bus Client Setup identification number.
 *
 * <p>The IdentificationNumber in its textual form consists of the last 8 digits of the 10 digits
 * serial number within the Equipment Identifier .<br>
 * The long value is the base-10 value calculated from the textual representation interpreted as
 * hexadecimal (base-16), the BCD value. This value is the one that is used for the
 * identification_number (attribute 6) of the DLMS M-Bus client (class ID 72).
 *
 * <p>For example:<br>
 * - Equipment Identifier: G0000001122334400<br>
 * (Meter code G00000, serial number 0011223344, year of manufacturing 00)<br>
 * - IdentificationNumber in textual form: "11223344"<br>
 * - Attribute 6 of MBus Client: 287454020
 *
 * @see MbusClientAttribute#IDENTIFICATION_NUMBER
 */
public class IdentificationNumber {

  private static final int HEX_RADIX = 16;
  private static final String IDENTIFICATION_NUMBER_REGEX = "\\d{1,8}";

  private final String stringRepresentation;

  private IdentificationNumber(final String identificationNumberAsString) {
    validateIdentificationNumber(identificationNumberAsString);
    if (StringUtils.isBlank(identificationNumberAsString)) {
      this.stringRepresentation = null;
    } else {
      /*
       * If a String of less than 8 digits is given, make sure it is
       * prefixed with zero digits up to a length of 8.
       */
      this.stringRepresentation =
          String.format("%08d", Integer.valueOf(identificationNumberAsString));
    }
  }

  public static IdentificationNumber fromBcdFormatAsLong(final Long identificationInBcdAsLong) {
    final String stringRepresentation = calculateStringRepresentation(identificationInBcdAsLong);
    return new IdentificationNumber(stringRepresentation);
  }

  public static IdentificationNumber fromStringRepresentation(final String identificationAsString) {
    return new IdentificationNumber(identificationAsString);
  }

  private static Long toBcdFormatAsLong(final String identificationNumberAsString) {
    if (StringUtils.isBlank(identificationNumberAsString)) {
      return null;
    }
    validateIdentificationNumber(identificationNumberAsString);
    return Long.parseLong(identificationNumberAsString, HEX_RADIX);
  }

  private static void validateIdentificationNumber(final String identificationNumber) {
    if (StringUtils.isNotBlank(identificationNumber)
        && !identificationNumber.matches(IDENTIFICATION_NUMBER_REGEX)) {
      throw new IllegalArgumentException(
          "IdentificationNumber must be at least 1 and at most 8 digits: \""
              + identificationNumber
              + "\"");
    }
  }

  private static String calculateStringRepresentation(final Long identificationInBcdAsLong) {
    if (identificationInBcdAsLong == null) {
      return null;
    }

    return String.format("%08X", identificationInBcdAsLong);
  }

  @Override
  public String toString() {
    return String.format(
        "IdentificationNumber[%s(%d)]",
        this.stringRepresentation, toBcdFormatAsLong(this.stringRepresentation));
  }

  /** @return a DataObject with the double-long-unsigned value of the identification number */
  public DataObject asDataObject() {
    if (StringUtils.isBlank(this.stringRepresentation)) {
      return DataObject.newNullData();
    }
    return DataObject.newUInteger32Data(toBcdFormatAsLong(this.stringRepresentation));
  }

  public Long getIdentificationNumberInBcdAsLong() {
    return toBcdFormatAsLong(this.stringRepresentation);
  }

  public String getStringRepresentation() {
    return this.stringRepresentation;
  }
}
