// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import java.nio.ByteBuffer;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;
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
 * <p>Example of the different representations of the identificationNumber for a device with
 * Equipment Identifier: G0000001122334400 (Meter code G00000, serial number 0011223344, year of
 * manufacturing 00):<br>
 * - Textual representation: "11223344"<br>
 * - Numerical representation: 11223344<br>
 * - Bcd representation as long: 287454020 (used in Attribute 6 of MBus Client)
 *
 * @see MbusClientAttribute#IDENTIFICATION_NUMBER
 */
public class IdentificationNumber {

  private static final int HEX_RADIX = 16;
  private static final String IDENTIFICATION_NUMBER_REGEX = "\\d{1,8}";

  private final String textualRepresentation;

  private IdentificationNumber(final String identificationNumberInTextualRepresentation) {
    validateIdentificationNumber(identificationNumberInTextualRepresentation);
    if (StringUtils.isBlank(identificationNumberInTextualRepresentation)) {
      this.textualRepresentation = null;
    } else {
      /*
       * If a String of less than 8 digits is given, make sure it is
       * prefixed with zero digits up to a length of 8.
       */
      this.textualRepresentation =
          String.format("%08d", Integer.valueOf(identificationNumberInTextualRepresentation));
    }
  }

  public static IdentificationNumber fromBcdRepresentationAsLong(
      final Long identificationInBcdAsLong) {
    final String textualRepresentation = calculateTextualRepresentation(identificationInBcdAsLong);
    return new IdentificationNumber(textualRepresentation);
  }

  public static IdentificationNumber fromTextualRepresentation(
      final String identificationInTextualRepresentaion) {
    return new IdentificationNumber(identificationInTextualRepresentaion);
  }

  public static IdentificationNumber fromNumericalRepresentation(
      final Long identificationInNumericalRepresentation) {
    return new IdentificationNumber(String.format("%08d", identificationInNumericalRepresentation));
  }

  private static Long toBcdRepresentationAsLong(
      final String identificationNumberInTextualRepresentation) {
    if (StringUtils.isBlank(identificationNumberInTextualRepresentation)) {
      return null;
    }
    validateIdentificationNumber(identificationNumberInTextualRepresentation);
    return Long.parseLong(identificationNumberInTextualRepresentation, HEX_RADIX);
  }

  private static void validateIdentificationNumber(
      final String identificationNumberInTextualRepresentation) {
    if (StringUtils.isNotBlank(identificationNumberInTextualRepresentation)
        && !identificationNumberInTextualRepresentation.matches(IDENTIFICATION_NUMBER_REGEX)) {
      throw new IllegalArgumentException(
          "IdentificationNumber must be at least 1 and at most 8 digits: \""
              + identificationNumberInTextualRepresentation
              + "\"");
    }
  }

  private static String calculateTextualRepresentation(final Long identificationInBcdAsLong) {
    if (identificationInBcdAsLong == null) {
      return null;
    }

    return String.format("%08X", identificationInBcdAsLong);
  }

  @Override
  public String toString() {
    return String.format(
        "IdentificationNumber[%s(%d)]",
        this.textualRepresentation, toBcdRepresentationAsLong(this.textualRepresentation));
  }

  /**
   * @return a DataObject with the double-long-unsigned value of the identification number
   */
  public DataObject asDataObjectInBcdRepresentation() {
    if (StringUtils.isBlank(this.textualRepresentation)) {
      return DataObject.newNullData();
    }
    return DataObject.newUInteger32Data(toBcdRepresentationAsLong(this.textualRepresentation));
  }

  public DataObject asDataObject() {
    if (StringUtils.isBlank(this.textualRepresentation)) {
      return DataObject.newNullData();
    }
    return DataObject.newUInteger32Data(this.getLongRepresentation());
  }

  public Long getIdentificationNumberInBcdRepresentationAsLong() {
    return toBcdRepresentationAsLong(this.textualRepresentation);
  }

  public String getTextualRepresentation() {
    return this.textualRepresentation;
  }

  public Long getLongRepresentation() {
    return Long.parseLong(this.getTextualRepresentation());
  }

  public Integer getIntRepresentation() throws ProtocolAdapterException {
    // Convert textual representation of deviceIdentificationNumber to long (eight bytes)
    final ByteBuffer longBytesBuffer = ByteBuffer.allocate(Long.BYTES);
    longBytesBuffer.putLong(0, Long.parseLong(this.textualRepresentation, 16));
    // First four bytes should be empty (zero)
    byte[] intBytes = Arrays.copyOfRange(longBytesBuffer.array(), 0, 4);
    ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
    if (buffer.put(intBytes).flip().getInt() != 0) {
      throw new ProtocolAdapterException(
          String.format(
              "IdentificationNumber %s cannot be represented as integer.",
              this.textualRepresentation));
    }
    // Convert the last four bytes to integer
    intBytes = Arrays.copyOfRange(longBytesBuffer.array(), 4, 8);
    buffer = ByteBuffer.allocate(Integer.BYTES);
    return buffer.put(intBytes).flip().getInt();
  }
}
