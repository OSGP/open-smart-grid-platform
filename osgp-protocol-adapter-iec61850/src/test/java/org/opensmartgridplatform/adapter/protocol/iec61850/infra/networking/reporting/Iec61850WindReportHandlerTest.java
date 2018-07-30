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

public class Iec61850WindReportHandlerTest {

    private static final Pattern NODE_PATTERN = Pattern
            .compile("\\A(.*)WIND([1-9]\\d*+)/(LLN0|DRCC|DGEN|MMXU|GGIO)([1-9]\\d*+)?\\.(.*)\\Z");

    @Test
    public void testNodeNameRegexPattern() {

        final String expectedServerName = "WAGO61850Server";

        this.ensureMatches("WAGO61850ServerWIND1/LLN0.Beh", expectedServerName, "1", "LLN0", null, "Beh");
        this.ensureMatches("WAGO61850ServerWIND1/DRCC1.OutWSet", expectedServerName, "1", "DRCC", "1", "OutWSet");
        this.ensureMatches("WAGO61850ServerWIND1/DGEN1.TotWh", expectedServerName, "1", "DGEN", "1", "TotWh");
        this.ensureMatches("WAGO61850ServerWIND1/MMXU1.TotW", expectedServerName, "1", "MMXU", "1", "TotW");
        this.ensureMatches("WAGO61850ServerWIND1/MMXU1.W.phsA", expectedServerName, "1", "MMXU", "1", "W.phsA");
        this.ensureMatches("WAGO61850ServerWIND1/MMXU2.W.phsB", expectedServerName, "1", "MMXU", "2", "W.phsB");
        this.ensureMatches("WAGO61850ServerWIND2/GGIO1.Alm1", expectedServerName, "2", "GGIO", "1", "Alm1");
        this.ensureMatches("WAGO61850ServerWIND10/GGIO1.Alm1", expectedServerName, "10", "GGIO", "1", "Alm1");
    }

    private void ensureMatches(final String reference, final String expectedServerName, final String expectedSystemId,
            final String expectedNodeName, final String expectedNodeId, final String expectedAttribute) {

        final Matcher reportMatcher = NODE_PATTERN.matcher(reference);

        assertTrue(reportMatcher.matches());

        assertEquals(expectedServerName, reportMatcher.group(1));
        assertEquals(expectedSystemId, reportMatcher.group(2));
        assertEquals(expectedNodeName, reportMatcher.group(3));
        assertEquals(expectedNodeId, reportMatcher.group(4));
        assertEquals(expectedAttribute, reportMatcher.group(5));

    }
}
