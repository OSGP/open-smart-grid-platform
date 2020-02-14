/**
 * Copyright 2018 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.cucumber.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.time.Duration;
import java.util.TimeZone;

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

import junit.runner.Version;

public abstract class AppBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppBase.class);

    private static final String END_RETVAL = "End. Retval = {}";

    @Option(name = "-report", metaVar = "DIR", usage = "Directory to produce test reports")
    private File reportDir;

    @Option(name = "-skip-xml-report",
            metaVar = "DIR",
            usage = "Suppress the JUnit XML report generation (for more logging)")
    private boolean skipXmlReport;

    /**
     * SonarQube reports issue S106 about using stderr/stdout. These issues are
     * suppressed.
     */
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
            return -1;
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

        final JUnitCommandLineParseResult jUnitCommandLineParseResult = JUnitCommandLineParseResult.parse(testClasses);

        if (!this.reportDir.exists()) {
            this.reportDir.mkdirs();
        }

        if (!this.skipXmlReport) {
            junit.addListener(new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
                @Override
                public void testStarted(final Description description) throws Exception {
                    this.formatter.setOutput(new FileOutputStream(
                            new File(AppBase.this.reportDir, "TEST-" + description.getDisplayName() + ".xml")));
                    super.testStarted(description);
                }
            });
        }

        final Result result = junit.run(jUnitCommandLineParseResult.createRequest(new Computer()));
        if (!result.getFailures().isEmpty()) {
            LOGGER.info("JUnit runner result for {} test(s) run, {} test(s) ignored, {} test(s) failed in {}:",
                    result.getRunCount(), result.getIgnoreCount(), result.getFailureCount(),
                    Duration.ofMillis(result.getRunTime()));
            result.getFailures().forEach(this::logFailureDetails);
        }
        return result.wasSuccessful() ? 0 : 1;
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
                .append(description.isTest() ? "test" : "suite with " + description.getChildren().size() + " children");
        if (description.equals(Description.TEST_MECHANISM)) {
            sb.append(" (resulted from a step in the test-running mechanism that went wrong)");
        }
        return sb.toString();
    }
}
