/*
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.execution;

import junit.framework.Test;
import junit.framework.TestResult;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.junit.runner.Description;

/** Wraps {@link Description} into {@link Test} enough to fake {@link JUnitResultFormatter}. */
public class DescriptionAsTest implements Test {
  private final Description description;

  public DescriptionAsTest(final Description description) {
    this.description = description;
  }

  @Override
  public int countTestCases() {
    return 1;
  }

  @Override
  public void run(final TestResult result) {
    throw new UnsupportedOperationException();
  }

  /** {@link JUnitResultFormatter} determines the test name by reflection. */
  public String getName() {
    return this.description.getDisplayName();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || this.getClass() != o.getClass()) {
      return false;
    }

    final DescriptionAsTest that = (DescriptionAsTest) o;

    return this.description.equals(that.description);
  }

  @Override
  public int hashCode() {
    return this.description.hashCode();
  }
}
