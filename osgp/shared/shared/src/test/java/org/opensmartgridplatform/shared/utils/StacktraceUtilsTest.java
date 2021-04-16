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

import org.junit.jupiter.api.Test;

public class StacktraceUtilsTest {
  @Test
  public void returnsCurrentStacktrace() {
    final String stacktrace = currentStacktrace();
    assertThat(stacktrace).startsWith(this.getClass().getCanonicalName()).contains("\n  at ");
  }
}
