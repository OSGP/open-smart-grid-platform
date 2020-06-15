/**
 * Copyright 2020 Smart Society Services B.V.
 */
package org.opensmartgridplatform.domain.core.util;

public final class SearchUtil {

    // The wildcards
    private static final String ZERO_OR_MORE = "%";
    private static final String SINGLE = "_";

    private SearchUtil() {
        throw new IllegalStateException("static Utility class not to be instantiated");
    }

    public static String getCleanedInput(final String input) {
        if (input == null) {
            return null;
        } else {
            return input.replace(ZERO_OR_MORE, "").replace(SINGLE, "");
        }
    }

}
