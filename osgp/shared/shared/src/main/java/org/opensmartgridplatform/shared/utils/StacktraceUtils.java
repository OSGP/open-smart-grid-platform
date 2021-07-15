/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
}
