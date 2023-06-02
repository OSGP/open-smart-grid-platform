//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.shared.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Convenience methods dealing with stacktraces. */
public class StacktraceUtils {
  private StacktraceUtils() {
    throw new UnsupportedOperationException("Not instantiable.");
  }

  public static String currentStacktrace() {
    return Stream.of(new Throwable().getStackTrace())
        .skip(1)
        .map(StackTraceElement::toString)
        .collect(Collectors.joining("\n  at "));
  }

  public static String messageAndCauses(final Throwable t) {
    final String className;
    if (t.getStackTrace().length > 0) {
      className = t.getStackTrace()[0].getClassName();
    } else {
      className = "<Unknown class>";
    }
    String result = "%nCaused by %s: %s".formatted(className, t.getMessage());
    if (t.getCause() != null) {
      result += messageAndCauses(t.getCause());
    }
    return result;
  }
}
