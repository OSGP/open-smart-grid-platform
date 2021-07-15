/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class TestReportingRegexPattern {

  private static final String DATA_SET_REF_RTU = "WAGO61850ServerRTU1/LLN0$Status";
  private static final String DATA_SET_REF_GA_FURNACE = "WAGO61850ServerGAS_FURNACE2/LLN0$Status";
  private static final String NODE_NAMES = "(RTU|PV|BATTERY|LOAD|CHP|HEAT_BUFFER|GAS_FURNACE)";

  // private static final String NODE_NAMES = "RTU";

  @Test
  public void testOriginalPattern() {
    final Pattern pattern = Pattern.compile("\\AWAGO61850ServerRTU([1-9]\\d*+)/LLN0\\$Status\\Z");
    final Matcher reportMatcher = pattern.matcher(DATA_SET_REF_RTU);
    final boolean patternMatches = reportMatcher.matches();
    assertThat(patternMatches).isTrue();
    final Integer value = Integer.parseInt(reportMatcher.group(1));
    assertThat(value).isEqualTo(Integer.valueOf(1));
  }

  @Test
  public void testNewPatternRtu() {
    final String patternString = "\\A(.*)" + NODE_NAMES + "([1-9]\\d*+)/LLN0\\$Status\\Z";
    final Pattern pattern = Pattern.compile(patternString);
    final Matcher reportMatcher = pattern.matcher(DATA_SET_REF_RTU);
    final boolean patternMatches = reportMatcher.matches();
    assertThat(patternMatches).isTrue();
    final Integer value = Integer.parseInt(reportMatcher.group(3));
    assertThat(value).isEqualTo(Integer.valueOf(1));
  }

  @Test
  public void testNewPatternGasFurnace() {
    final String patternString = "\\A(.*)" + NODE_NAMES + "([1-9]\\d*+)/LLN0\\$Status\\Z";
    final Pattern pattern = Pattern.compile(patternString);
    final Matcher reportMatcher = pattern.matcher(DATA_SET_REF_GA_FURNACE);
    final boolean patternMatches = reportMatcher.matches();
    assertThat(patternMatches).isTrue();
    final Integer value = Integer.parseInt(reportMatcher.group(3));
    assertThat(value).isEqualTo(Integer.valueOf(2));
  }
}
