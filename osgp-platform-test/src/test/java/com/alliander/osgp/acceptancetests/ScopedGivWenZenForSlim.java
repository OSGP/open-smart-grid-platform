package com.alliander.osgp.acceptancetests;

import java.util.TimeZone;

import org.givwenzen.GivWenZenExecutorCreator;
import org.givwenzen.GivWenZenForSlim;
import org.givwenzen.annotations.InstantiationStrategy;
import org.joda.time.DateTimeZone;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.alliander.osgp.acceptancetests.config.TestApplicationContext;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * A testfixture for GivWenZen which allows to change the location to search for
 * DomainSteps.
 * 
 * Setup in Fitnesse:
 * 
 * | import | | com.alliander.osp.platform.acceptancetests |
 * 
 * | script | | start | scoped giv wen zen for slim | [identifier]
 * 
 * For example: | start | scoped giv wen zen for slim | deviceinstallation will
 * search DomainSteps in package:
 * com.alliander.osp.platform.acceptancetests.deviceinstallation
 */
public class ScopedGivWenZenForSlim extends GivWenZenForSlim {

    private static AnnotationConfigApplicationContext rootContext;

    private static InstantiationStrategy autowireStepDefinitionClassesWithSpring() {
        rootContext = new AnnotationConfigApplicationContext();

        // Force local timezone to UTC (like platform)
        DateTimeZone.setDefault(DateTimeZone.UTC);
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        // Set loglevel to INFO (instead of DEBUG)
        final Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        rootContext.register(TestApplicationContext.class);
        try {
            rootContext.refresh();
        } catch (final Exception e) {
            // just for debugging...
            throw e;
        }
        return new SpringInstantiationStrategy(rootContext);
    }

    /**
     * Create instance of ScopedGivWenZenForSlim Override the DomainStepFinder
     * instance.
     * 
     * @param testIdentification
     *            sub package
     */
    public ScopedGivWenZenForSlim(final String testIdentification) {
        super(GivWenZenExecutorCreator.instance().stepClassBasePackage(ScopedGivWenZenForSlim.class.getPackage().getName() + "." + testIdentification + ".")
                .customParserFinder(new ScopedCustomParserFinder(ScopedGivWenZenForSlim.class.getPackage().getName() + "." + testIdentification + "."))
                .customInstantiationStrategies(autowireStepDefinitionClassesWithSpring()).create());
        // System.out.println("Testcase scope: " +
        // ScopedGivWenZenForSlim.class.getPackage().getName() + "." +
        // testIdentification);
    }

    public static GenericApplicationContext getContext() {
        return rootContext;
    }
}
