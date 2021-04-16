/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

public class SearchUtil {

  // The escape character for search expressions.
  //
  // It's not possible to simply replace the '\' character by something else.
  // If it's necessary to use another character, investigate what other
  // changes are needed in the code.
  private static final String ESCAPE = "\\";

  // The wildcards, used for filtering and their replacements
  private static final String WILDCARD_ALL = "*";
  private static final String WILDCARD_SINGLE = "?";
  private static final String WILDCARD_ALL_REPLACEMENT = "%";
  private static final String WILDCARD_SINGLE_REPLACEMENT = "_";

  private SearchUtil() {
    throw new IllegalStateException("static Utility class not to be instantiated");
  }

  /**
   * Replaces normal wildcards by SQL wildcards and escapes SQL wildcards that were already present.
   *
   * @param input String
   * @return an output String containing the correct wildcards.
   */
  public static String replaceAndEscapeWildcards(final String input) {
    if (input == null) {
      return null;
    } else {
      return input
          .replace(ESCAPE, ESCAPE + ESCAPE)
          .replace(WILDCARD_ALL_REPLACEMENT, ESCAPE + WILDCARD_ALL_REPLACEMENT)
          .replace(WILDCARD_SINGLE_REPLACEMENT, ESCAPE + WILDCARD_SINGLE_REPLACEMENT)
          .replace(WILDCARD_ALL, WILDCARD_ALL_REPLACEMENT)
          .replace(WILDCARD_SINGLE, WILDCARD_SINGLE_REPLACEMENT);
    }
  }

  /**
   * Replaces normal wildcards by SQL wildcards
   *
   * @param input String
   * @return an output String containing the correct wildcards.
   */
  public static String replaceWildcards(final String input) {
    if (input == null) {
      return null;
    } else {
      return input
          .replace(WILDCARD_ALL, WILDCARD_ALL_REPLACEMENT)
          .replace(WILDCARD_SINGLE, WILDCARD_SINGLE_REPLACEMENT);
    }
  }
}
