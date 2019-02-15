package org.opensmartgridplatform.shared.utils;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Convenience methods dealing with stacktraces. */
public class StacktraceUtils {
    public static String currentStacktrace() {
        return Stream.of(new Throwable().getStackTrace()).skip(1).map(StackTraceElement::toString).collect(Collectors.joining("\n  at "));
    }
}
