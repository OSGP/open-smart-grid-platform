package com.alliander.osgp.cucumber.execution;

import java.io.File;
import java.io.FileOutputStream;
import java.util.TimeZone;

import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
import org.joda.time.DateTimeZone;
import org.junit.internal.JUnitSystem;
import org.junit.internal.RealSystem;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.runner.Version;

public abstract class AppBase {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppBase.class);

    @Option(name = "-report", metaVar = "DIR", usage = "Directory to produce test reports")
    private File reportDir;

    @Option(name = "-skip-xml-report", metaVar = "DIR", usage = "Suppress the JUnit XML report generation (for more logging)")
    private boolean skipXmlReport;

    public static int run(final AppBase app, final String[] testClasses, final String... args) {
        // Ensure the tests are executed in UTC time
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        DateTimeZone.setDefault(DateTimeZone.UTC);

        final CmdLineParser p = new CmdLineParser(app);
        try {
            p.parseArgument(args);
            LOGGER.info("Start");
            final int retval = app.runTests(testClasses);
            LOGGER.info("End. Retval = {}", retval);

            return retval;
        } catch (final CmdLineException e) {
            LOGGER.error("Incorrect usage", e);
            LOGGER.error("java -jar <...>.jar [opts] ...");
            p.printUsage(System.err);
            return -1;
        }
    }

    private int runTests(final String[] testClasses) {

        final JUnitCore junit = new JUnitCore();

        final JUnitSystem system = new RealSystem();

        system.out().println("JUnit version " + Version.id());

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
        return result.wasSuccessful() ? 0 : 1;
    }
}
