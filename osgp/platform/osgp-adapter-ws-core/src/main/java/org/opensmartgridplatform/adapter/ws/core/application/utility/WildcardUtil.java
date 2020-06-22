package org.opensmartgridplatform.adapter.ws.core.application.utility;

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
    public static String replaceWildcards(String input) {
        return input.replace(ESCAPE, ESCAPE + ESCAPE)
                .replace(WILDCARD_ALL_REPLACEMENT, ESCAPE + WILDCARD_ALL_REPLACEMENT)
                .replace(WILDCARD_SINGLE_REPLACEMENT, ESCAPE + WILDCARD_SINGLE_REPLACEMENT)
                .replace(WILDCARD_ALL, WILDCARD_ALL_REPLACEMENT)
                .replace(WILDCARD_SINGLE, WILDCARD_SINGLE_REPLACEMENT)
                .toUpperCase();
    }

}
