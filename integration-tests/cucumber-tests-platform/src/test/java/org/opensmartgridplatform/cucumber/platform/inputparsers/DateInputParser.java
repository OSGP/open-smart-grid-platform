// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.platform.inputparsers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Parses the date of a cucumber input string to a java.util.Date instance.
 *
 * <p>Use this class to ensure the same format is used over all scenarios, and suppress any parsing
 * exception to avoid clutter in the builders.
 */
public class DateInputParser {

  private static final DateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

  /**
   * Parses the date of a cucumber input string to a java.util.Date instance.
   *
   * @param input string with format "yyyy-MM-dd"
   * @return Date instance
   */
  public static Date parse(final String input) {
    try {
      return FORMAT.parse(input);
    } catch (final ParseException e) {
      throw new AssertionError(
          String.format("Input date '%s' could not be parsed to a date object.", input));
    }
  }
}
