/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class TestReportingRegexPattern {

    private static final String DATA_SET_REF_RTU = "WAGO61850ServerRTU1/LLN0$Status";
    private static final String DATA_SET_REF_GA_FURNACE = "WAGO61850ServerGAS_FURNACE2/LLN0$Status";
    private static final String NODE_NAMES = "(RTU|PV|BATTERY|LOAD|CHP|HEAT_BUFFER|GAS_FURNACE)";

    // private static final String NODE_NAMES = "RTU";

    @Test
    public void testOriginalPattern() {
        final Pattern pattern = Pattern.compile("\\AWAGO61850ServerRTU([1-9]\\d*+)/LLN0\\$Status\\Z");
        Matcher reportMatcher = pattern.matcher(DATA_SET_REF_RTU);
        final boolean patternMatches = reportMatcher.matches();
        assertTrue(patternMatches);
        final Integer value = Integer.parseInt(reportMatcher.group(1));
        assertEquals(new Integer(1), value);
    }

    @Test
    public void testNewPatternRtu() {
        final String patternString = "\\A(.*)" + NODE_NAMES + "([1-9]\\d*+)/LLN0\\$Status\\Z";
        final Pattern pattern = Pattern.compile(patternString);
        Matcher reportMatcher = pattern.matcher(DATA_SET_REF_RTU);
        final boolean patternMatches = reportMatcher.matches();
        assertTrue(patternMatches);
        final Integer value = Integer.parseInt(reportMatcher.group(3));
        assertEquals(new Integer(1), value);
    }

    @Test
    public void testNewPatternGasFurnace() {
        final String patternString = "\\A(.*)" + NODE_NAMES + "([1-9]\\d*+)/LLN0\\$Status\\Z";
        final Pattern pattern = Pattern.compile(patternString);
        Matcher reportMatcher = pattern.matcher(DATA_SET_REF_GA_FURNACE);
        final boolean patternMatches = reportMatcher.matches();
        assertTrue(patternMatches);
        final Integer value = Integer.parseInt(reportMatcher.group(3));
        assertEquals(new Integer(2), value);
    }
}
