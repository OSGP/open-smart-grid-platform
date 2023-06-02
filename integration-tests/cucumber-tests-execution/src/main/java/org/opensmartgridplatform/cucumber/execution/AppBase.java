//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.cucumber.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import junit.runner.Version;
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.joda.time.DateTimeZone;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AppBase {

  private static final int SUCCESS = 0;
  private static final int FAILURE = 1;
  private static final int INCORRECT_USAGE = -1;
  private static final int ISSUES_IN_TEST_MECHANISM = -2;

  private static final Logger LOGGER = LoggerFactory.getLogger(AppBase.class);

  private static final String END_RETVAL = "End. Retval = {}";

  @Option(name = "-report", metaVar = "DIR", usage = "Directory to produce test reports")
  private File reportDir;

  @Option(
      name = "-skip-xml-report",
      metaVar = "DIR",
      usage = "Suppress the JUnit XML report generation (for more logging)")
  private boolean skipXmlReport;

  /** SonarQube reports issue S106 about using stderr/stdout. These issues are suppressed. */
  @SuppressWarnings("squid:S106")
  public static int run(final AppBase app, final String[] testClasses, final String... args) {
    // Ensure the tests are executed in UTC time
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    DateTimeZone.setDefault(DateTimeZone.UTC);

    final CmdLineParser p = new CmdLineParser(app);
    try {
      p.parseArgument(args);
      LOGGER.info("Start");
      final int retval = app.runTests(testClasses);
      LOGGER.info(END_RETVAL, retval);

      return retval;
    } catch (final CmdLineException e) {
      LOGGER.error("Incorrect usage", e);
      LOGGER.error("java -jar <...>.jar [opts] ...");
      p.printUsage(System.err);
      return INCORRECT_USAGE;
    } catch (final Exception e) {
      LOGGER.error("Caught Exception", e);
      final int retval = 1;
      LOGGER.info(END_RETVAL, retval);
      return retval;
    }
  }

  private int runTests(final String[] testClasses) {

    final JUnitCore junit = new JUnitCore();

    LOGGER.info("JUnit version {}", Version.id());

    final JUnitCommandLineParseResult jUnitCommandLineParseResult =
        JUnitCommandLineParseResult.parse(testClasses);

    if (!this.reportDir.exists()) {
      this.reportDir.mkdirs();
    }

    if (!this.skipXmlReport) {
      junit.addListener(
          new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
            @Override
            public void testStarted(final Description description) throws Exception {
              this.formatter.setOutput(
                  new FileOutputStream(
                      new File(
                          AppBase.this.reportDir,
                          "TEST-" + description.getDisplayName() + ".xml")));
              super.testStarted(description);
            }
          });
    }

    final Result result = junit.run(jUnitCommandLineParseResult.createRequest(new Computer()));

    final List<Failure> failuresLinkedToTestMechanism =
        result.getFailures().stream()
            .filter(failure -> Description.TEST_MECHANISM.equals(failure.getDescription()))
            .collect(Collectors.toList());

    final int testMechanismRelatedFailureCount = failuresLinkedToTestMechanism.size();
    if (testMechanismRelatedFailureCount > 0) {
      LOGGER.warn(
          "The test run has lead to {} failure(s) related to the test mechanism",
          testMechanismRelatedFailureCount);
      failuresLinkedToTestMechanism.forEach(this::logFailureDetails);
    }

    final int failureCount = result.getFailureCount();
    if (result.getRunCount() == 0 && failureCount > 0) {
      LOGGER.error(
          "Test result has {} failures, while no tests were executed. "
              + "Fail the build since this probably indicates issues in setting up the test context.",
          failureCount);
      return FAILURE;
    }

    final int failuresLinkedToTests = failureCount - testMechanismRelatedFailureCount;

    if (failureCount > 0 && failuresLinkedToTests == 0) {
      LOGGER.error(
          "Test result has {} failures, while none of these were directly related to tests that were executed. "
              + "Returning exceptional value {} instead of {} to indicate issues without failing the build.",
          failureCount,
          ISSUES_IN_TEST_MECHANISM,
          SUCCESS);
      return ISSUES_IN_TEST_MECHANISM;
    }

    return SUCCESS;
  }

  private void logFailureDetails(final Failure failure) {
    final Description description = failure.getDescription();
    final Throwable thrownException = failure.getException();
    final String failureDescription = this.failureDescription(description);
    LOGGER.warn("Failure: {}", failureDescription, thrownException);
  }

  private String failureDescription(final Description description) {
    final StringBuilder sb = new StringBuilder();
    sb.append(description.getDisplayName()).append(System.lineSeparator());
    sb.append("\tclassName: ").append(description.getClassName()).append(System.lineSeparator());
    sb.append("\tmethodName: ").append(description.getMethodName()).append(System.lineSeparator());
    sb.append("\ttestClass: ")
        .append(description.getTestClass() == null ? "" : description.getTestClass().getName())
        .append(System.lineSeparator());
    sb.append("\ttype: ")
        .append(
            description.isTest()
                ? "test"
                : "suite with " + description.getChildren().size() + " children");
    if (description.equals(Description.TEST_MECHANISM)) {
      sb.append(" (resulted from a step in the test-running mechanism that went wrong)");
    }
    return sb.toString();
  }
}
