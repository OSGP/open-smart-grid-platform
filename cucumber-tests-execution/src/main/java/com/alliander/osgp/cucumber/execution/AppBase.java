package com.alliander.osgp.cucumber.execution;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter;
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
    File reportDir;

    public static int run(final AppBase app, final String[] testClasses, final String... args) throws Exception {
        final CmdLineParser p = new CmdLineParser(app);
        try {
            p.parseArgument(args);
            LOGGER.info("Start");
            final int retval = app.runTests(testClasses);
            LOGGER.info("End. Retval = {}", retval);

            return retval;
        } catch (final CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar <...>.jar [opts] ...");
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

        junit.addListener(new JUnitResultFormatterAsRunListener(new XMLJUnitResultFormatter()) {
            @Override
            public void testStarted(final Description description) throws Exception {
                this.formatter.setOutput(new FileOutputStream(
                        new File(AppBase.this.reportDir, "TEST-" + description.getDisplayName() + ".xml")));
                super.testStarted(description);
            }
        });

        final Result result = junit.run(jUnitCommandLineParseResult.createRequest(new Computer()));
        return result.wasSuccessful() ? 0 : 1;
    }
}
