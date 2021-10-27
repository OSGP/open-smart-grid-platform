/*
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.dlms.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * LogicalDeviceIds can be passed in configuration or parameters as:
 *
 * <ul>
 *   <li>id
 *   <li>id,id,id
 *   <li>from..to - id
 * </ul>
 *
 * This class takes the input and converts it to an int array. If no match is found, an empty array
 * is returned.
 */
public class LogicalDeviceIdsConverter {

  private static final String REGEX_SEPARATED = "((\\d*),{0,1})*";
  private static final String REGEX_RANGE = "(\\d*)\\.\\.(\\d*)";

  private LogicalDeviceIdsConverter() {
    // Hide public constructor
  }

  public static int[] convert(final String logicalDeviceIds) {
    int[] result;

    result = convertRange(logicalDeviceIds);
    if (result.length > 0) {
      return result;
    }

    result = convertSeparated(logicalDeviceIds);
    return result;
  }

  private static int[] convertRange(final String logicalDeviceIds) {
    final Pattern rangePattern = Pattern.compile(REGEX_RANGE);
    final Matcher rangeMatcher = rangePattern.matcher(logicalDeviceIds);

    if (rangeMatcher.matches()) {
      final int from = Integer.parseInt(rangeMatcher.group(1));
      final int to = Integer.parseInt(rangeMatcher.group(2));
      final int size = (to - from) + 1;

      final int[] result = new int[size];
      int index = 0;
      for (int i = from; i <= to; i++) {
        result[index++] = i;
      }

      return result;
    }

    return new int[0];
  }

  private static int[] convertSeparated(final String logicalDeviceIds) {
    final Pattern separatedPattern = Pattern.compile(REGEX_SEPARATED);
    final Matcher separatedMatcher = separatedPattern.matcher(logicalDeviceIds);

    if (separatedMatcher.matches()) {
      final String[] ids = logicalDeviceIds.split(",");
      final int[] result = new int[ids.length];

      for (int i = 0; i < ids.length; i++) {
        result[i] = Integer.parseInt(ids[i]);
      }

      return result;
    }

    return new int[0];
  }
}
