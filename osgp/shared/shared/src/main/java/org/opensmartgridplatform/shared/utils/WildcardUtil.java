/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.utils;

public class WildcardUtil {

    // The wildcard, used for filtering.
    private static final String ESCAPE = "\\";
    private static final String WILDCARD_ALL = "*";
    private static final String WILDCARD_SINGLE = "?";
    private static final String WILDCARD_ALL_REPLACEMENT = "%";
    private static final String WILDCARD_SINGLE_REPLACEMENT = "_";

    private WildcardUtil() {
        throw new IllegalStateException("static Utility class not to be instantiated");
    }

    /**
     * Replaces normal wildcards by SQL wildcards and escapes SQL wildcards that
     * were already present.
     *
     * @param input
     *            String
     * @return an output String containing the correct wildcards.
     */
    public static String replaceWildcards(final String input) {
        return input.replace(ESCAPE, ESCAPE + ESCAPE)
                .replace(WILDCARD_ALL_REPLACEMENT, ESCAPE + WILDCARD_ALL_REPLACEMENT)
                .replace(WILDCARD_SINGLE_REPLACEMENT, ESCAPE + WILDCARD_SINGLE_REPLACEMENT)
                .replace(WILDCARD_ALL, WILDCARD_ALL_REPLACEMENT)
                .replace(WILDCARD_SINGLE, WILDCARD_SINGLE_REPLACEMENT)
                .toUpperCase();
    }

    /**
     * Checks whether the input contains a wildcard.
     *
     * @param input
     * @return true or false
     */
    public static boolean hasWildcards(final String input) {
        return input.contains(WILDCARD_ALL) || input.contains(WILDCARD_SINGLE);
    }

}
