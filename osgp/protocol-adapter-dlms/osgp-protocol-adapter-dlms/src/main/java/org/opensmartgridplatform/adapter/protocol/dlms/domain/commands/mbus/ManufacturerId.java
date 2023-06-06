// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.mbus;

import org.apache.commons.lang3.StringUtils;
import org.openmuc.jdlms.datatypes.DataObject;
import org.opensmartgridplatform.dlms.interfaceclass.attribute.MbusClientAttribute;

/**
 * Represents the M-Bus Client Setup Manufacturer ID.
 *
 * <p>The ManufacturerId in its textual form is numerically encoded in a kind of base-32 encoding
 * with a restricted set of values (AAA..ZZZ).<br>
 * Each of the three digits of a manufacturer ID is an upper case letter where 'A' -> 1, 'B' -> 2
 * and 'Z' -> 26. Since ASCII('A') = 65, the numerical value for each of the digits can be
 * calculated by subtracting 64 from the ASCII value.<br>
 * The integer value is the base-10 value calculated from the base-32 representation as described
 * above. This value is the one that is used for the manufacturer_id (attribute 7) of the DLMS M-Bus
 * client (class ID 72).
 *
 * <p>The DLMS User Association has a list of appointed IDs.
 *
 * @see MbusClientAttribute#MANUFACTURER_ID
 * @see <a href="http://www.dlms.com/organization/flagmanufacturesids/">FLAG Manufacturers ID</a>
 */
public class ManufacturerId {

  private static final int MANUFACTURER_ID_BASE = 32;
  private static final int MANUFACTURER_ID_CHAR_FLOOR = 64;
  private static final String MANUFACTURER_ID_REGEX = "[A-Z]{3}";

  private static final int MIN_ID =
      1 * MANUFACTURER_ID_BASE * MANUFACTURER_ID_BASE + 1 * MANUFACTURER_ID_BASE + 1;
  private static final int MAX_ID =
      26 * MANUFACTURER_ID_BASE * MANUFACTURER_ID_BASE + 26 * MANUFACTURER_ID_BASE + 26;

  private final String identification;

  private ManufacturerId(final String identification) {
    validateIdentification(identification);
    this.identification = identification;
  }

  public static ManufacturerId fromIdentification(final String identification) {
    return new ManufacturerId(identification);
  }

  public static ManufacturerId fromId(final int id) {
    final String manufacturerIdentification = calculateIdentification(id);
    try {
      validateIdentification(manufacturerIdentification);
    } catch (final IllegalArgumentException e) {
      throw new IllegalArgumentException("id must represent a manufacturer ID: " + id, e);
    }
    return new ManufacturerId(manufacturerIdentification);
  }

  public static ManufacturerId fromDataObject(final DataObject dataObject) {
    final int id;
    if (dataObject == null || dataObject.isNull()) {
      id = 0;
    } else if (DataObject.Type.LONG_UNSIGNED != dataObject.getType()) {
      throw new IllegalArgumentException(
          "dataObject type must be long-unsigned: " + dataObject.getType());
    } else {
      id = ((Number) dataObject.getRawValue()).intValue();
    }
    return fromId(id);
  }

  /**
   * Calculates the integer value of the DLMS M-Bus client setup manufacturer_id from the given
   * manufacturer identification, according to EN 62056-21.
   *
   * <p>
   *
   * <table summary="Calculation of int value for a FLAG Manufacturer ID">
   * <tr>
   * <td>&nbsp;</td>
   * <td>(ASCII(letter1) - 64) * 32 * 32</td>
   * </tr>
   * <tr>
   * <td>+</td>
   * <td>(ASCII(letter2) - 64) * 32</td>
   * </tr>
   * <tr>
   * <td>+</td>
   * <td>(ASCII(letter3) - 64)</td>
   * </tr>
   * </table>
   *
   * <p>ASCII('A') = 65, ASCII('B') = 66, ..., ASCII('Z') = 90
   *
   * @param identification manufacturer identification in three capital letters
   * @return DLMS manufacturer ID
   */
  private static int calculateId(final String identification) {
    if (StringUtils.isBlank(identification)) {
      return 0;
    }
    validateIdentification(identification);
    final char[] chars = identification.toCharArray();
    int result = 0;
    result += (chars[0] - MANUFACTURER_ID_CHAR_FLOOR) * MANUFACTURER_ID_BASE * MANUFACTURER_ID_BASE;
    result += (chars[1] - MANUFACTURER_ID_CHAR_FLOOR) * MANUFACTURER_ID_BASE;
    result += (chars[2] - MANUFACTURER_ID_CHAR_FLOOR);
    return result;
  }

  private static void validateIdentification(final String identification) {
    if (StringUtils.isNotBlank(identification) && !identification.matches(MANUFACTURER_ID_REGEX)) {
      throw new IllegalArgumentException(
          "identification must be three upper case letters: \"" + identification + "\"");
    }
  }

  /**
   * Calculates the integer value of the DLMS M-Bus client setup manufacturer_id from the given
   * manufacturer identification, according to EN 62056-21.
   *
   * @param id manufacturer identification in three capital letters
   * @return DLMS manufacturer ID
   */
  private static String calculateIdentification(final int id) {
    if (id == 0) {
      return null;
    }
    if (id < MIN_ID || id > MAX_ID) {
      throw new IllegalArgumentException("id not in [" + MIN_ID + ".." + MAX_ID + "]: " + id);
    }
    /*
     * The last character is the remainder of the id value and the encoding
     * base increased with the floor value. For each character coming before
     * it use the quotient of the previous value and the encoding base as
     * input to calculate the next remainder.
     */
    final char[] chars = new char[3];
    int remainder = id % MANUFACTURER_ID_BASE;
    chars[2] = (char) (MANUFACTURER_ID_CHAR_FLOOR + remainder);
    int quotient = id / MANUFACTURER_ID_BASE;
    remainder = quotient % MANUFACTURER_ID_BASE;
    chars[1] = (char) (MANUFACTURER_ID_CHAR_FLOOR + remainder);
    quotient = quotient / MANUFACTURER_ID_BASE;
    remainder = quotient % MANUFACTURER_ID_BASE;
    chars[0] = (char) (MANUFACTURER_ID_CHAR_FLOOR + remainder);
    return new String(chars);
  }

  @Override
  public String toString() {
    return String.format(
        "ManufacturerId[%s(%d)]", this.identification, calculateId(this.identification));
  }

  /**
   * @return a DataObject with the long-unsigned value of the manufacturer id
   */
  public DataObject asDataObject() {
    return DataObject.newUInteger16Data(calculateId(this.identification));
  }

  public String getIdentification() {
    return this.identification;
  }

  public int getId() {
    return calculateId(this.identification);
  }
}
