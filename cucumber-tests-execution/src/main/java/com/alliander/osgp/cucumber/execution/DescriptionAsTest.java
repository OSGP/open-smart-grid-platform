package com.alliander.osgp.cucumber.execution;

import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.junit.runner.Description;

import junit.framework.Test;
import junit.framework.TestResult;

/**
 * Wraps {@link Description} into {@link Test} enough to fake
 * {@link JUnitResultFormatter}.
 */
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

    /**
     * {@link JUnitResultFormatter} determines the test name by reflection.
     */
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

        if (!this.description.equals(that.description)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return this.description.hashCode();
    }
}