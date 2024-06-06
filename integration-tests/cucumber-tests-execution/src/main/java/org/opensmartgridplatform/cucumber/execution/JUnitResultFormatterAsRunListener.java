// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.cucumber.execution;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Adopts {@link JUnitResultFormatter} into {@link RunListener}, and also captures stdout/stderr by
 * intercepting the likes of {@link System#out}.
 *
 * <p>Because Ant JUnit formatter uses one stderr/stdout per one test suite, we capture each test
 * case into a separate report file.
 *
 * <p>SonarQube reports issue S106 about using stderr/stdout. These issues are suppressed.
 */
public class JUnitResultFormatterAsRunListener extends RunListener {
  protected final JUnitResultFormatter formatter;
  private ByteArrayOutputStream stdout, stderr;
  private PrintStream oldStdout, oldStderr;
  private int problem;
  private long startTime;

  public JUnitResultFormatterAsRunListener(final JUnitResultFormatter formatter) {
    this.formatter = formatter;
  }

  @Override
  public void testRunStarted(final Description description) throws Exception {
    // Empty body.
  }

  @Override
  public void testRunFinished(final Result result) throws Exception {
    // Empty body.
  }

  @SuppressWarnings("squid:S106")
  @Override
  public void testStarted(final Description description) throws Exception {
    this.formatter.startTestSuite(new JUnitTest(description.getDisplayName()));
    this.formatter.startTest(new DescriptionAsTest(description));
    this.problem = 0;
    this.startTime = System.currentTimeMillis();

    this.oldStdout = System.out;
    this.oldStderr = System.err;

    this.stdout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(this.stdout));

    this.stderr = new ByteArrayOutputStream();
    System.setErr(new PrintStream(this.stderr));
  }

  @SuppressWarnings("squid:S106")
  @Override
  public void testFinished(final Description description) throws Exception {
    System.out.flush();
    System.err.flush();
    System.setOut(this.oldStdout);
    System.setErr(this.oldStderr);

    this.formatter.setSystemOutput(this.stdout.toString());
    this.formatter.setSystemError(this.stderr.toString());
    this.formatter.endTest(new DescriptionAsTest(description));

    final JUnitTest suite = new JUnitTest(description.getDisplayName());
    suite.setCounts(1, this.problem, 0);
    suite.setRunTime(System.currentTimeMillis() - this.startTime);
    this.formatter.endTestSuite(suite);
  }

  @Override
  public void testFailure(final Failure failure) throws Exception {
    this.testAssumptionFailure(failure);
  }

  @Override
  public void testAssumptionFailure(final Failure failure) {
    this.problem++;
    this.formatter.addError(
        new DescriptionAsTest(failure.getDescription()), failure.getException());
  }
}
