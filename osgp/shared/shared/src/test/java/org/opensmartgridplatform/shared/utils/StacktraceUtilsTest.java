package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.opensmartgridplatform.shared.utils.StacktraceUtils.currentStacktrace;

import org.junit.Test;

public class StacktraceUtilsTest {
    @Test
    public void returnsCurrentStacktrace() {
        final String stacktrace = currentStacktrace();
        assertThat(stacktrace).startsWith(this.getClass().getCanonicalName()).contains("\n  at ");
    }
}