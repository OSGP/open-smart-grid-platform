/*
 * Copyright 2019 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.shared.utils.StacktraceUtils.currentStacktrace;
import static org.opensmartgridplatform.shared.utils.StacktraceUtils.messageAndCauses;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class StacktraceUtilsTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(StacktraceUtilsTest.class);

  @Test
  void returnsCurrentStacktrace() {
    final String stacktrace = currentStacktrace();
    assertThat(stacktrace).startsWith(this.getClass().getCanonicalName()).contains("\n  at ");
  }

  @Test
  void shouldOnlyReturnMessageAndCauses() {
    try {
      this.throwsNestedException();
    } catch (final RuntimeException e) {
      final String s = messageAndCauses(e);
      LOGGER.error("Example logging, this is what happened:{}", s);

      final List<String> causeLines = s.lines().toList();
      assertThat(causeLines).hasSize(3);
      assertThat(causeLines.get(0)).as("Newline after exception message").isBlank();
      assertThat(causeLines.get(1)).contains("over there");
      assertThat(causeLines.get(2)).contains("original error");
      assertThat(s)
          .as("Doesn't contain Java filenames and line numbers")
          .doesNotContainPattern("(.*.java:[0-9]*)");
    }
  }

  private void throwsNestedException() {
    try {
      this.throwsFirstException();
    } catch (final RuntimeException e) {
      throw new RuntimeException("Something went wrong over there", e);
    }
  }

  private void throwsFirstException() {
    throw new RuntimeException("This is the original error");
  }
}
