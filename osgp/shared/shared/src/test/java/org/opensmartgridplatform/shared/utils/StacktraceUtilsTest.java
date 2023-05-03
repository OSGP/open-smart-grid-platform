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
    // Arrange
    final var firstException = new RuntimeException("This is the original error");
    final var secondException = new RuntimeException("Something went wrong over there", firstException);

    final var expected =
        """

            Caused by org.opensmartgridplatform.shared.utils.StacktraceUtilsTest: Something went wrong over there
            Caused by org.opensmartgridplatform.shared.utils.StacktraceUtilsTest: This is the original error""";

    // Act
    final var actual = messageAndCauses(secondException);

    // Assert
    assertThat(actual).isEqualTo(expected);
  }
}
