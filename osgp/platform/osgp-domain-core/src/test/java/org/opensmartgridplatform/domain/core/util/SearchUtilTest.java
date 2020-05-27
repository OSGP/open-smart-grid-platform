/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.domain.core.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SearchUtilTest {

    @Test
    void testNormalInput() {
        final String input = "normal input without wildcards";
        assertThat(SearchUtil.getCleanedInput(input)).isEqualTo(input);
    }

    @Test
    void testNullInput() {
        assertThat(SearchUtil.getCleanedInput(null)).isNull();
    }

    @Test
    void testSingleWildcard() {

        final String input = "input with _single wildcard";
        final String expected = "input with single wildcard";

        assertThat(SearchUtil.getCleanedInput(input)).isEqualTo(expected);
    }

    @Test
    void testWildcard() {

        final String input = "input with %wildcard";
        final String expected = "input with wildcard";

        assertThat(SearchUtil.getCleanedInput(input)).isEqualTo(expected);
    }

    @Test
    void testBothWildcards() {

        final String input = "input with_ %wildcards";
        final String expected = "input with wildcards";

        assertThat(SearchUtil.getCleanedInput(input)).isEqualTo(expected);
    }
}
