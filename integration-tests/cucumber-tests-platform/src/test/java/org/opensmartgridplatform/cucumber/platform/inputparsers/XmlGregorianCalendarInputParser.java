// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.inputparsers;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Parses the date of a cucumber input string to a javax.xml.datatype.XMLGregorianCalendar instance.
 */
public class XmlGregorianCalendarInputParser {

  private XmlGregorianCalendarInputParser() {
    // Private constructor for utility class.
  }

  /**
   * Parses the date of a cucumber input string to a javax.xml.datatype.XMLGregorianCalendar
   * instance.
   *
   * @param input lexical representation of one the eight XML Schema date/time datatypes.
   * @return XMLGregorianCalendar instance
   */
  public static XMLGregorianCalendar parse(final String input) {
    try {
      return DatatypeFactory.newInstance().newXMLGregorianCalendar(input);
    } catch (final Exception e) {
      throw new AssertionError(
          String.format("Input '%s' could not be parsed to a XMLGregorianCalendar object.", input));
    }
  }
}
