// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.iec61850.infra.networking.reporting;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class Iec61850WindReportHandlerTest {

  private static final Pattern NODE_PATTERN =
      Pattern.compile("\\A(.*)WIND([1-9]\\d*+)/(LLN0|DRCC|DGEN|MMXU|GGIO)([1-9]\\d*+)?\\.(.*)\\Z");

  @Test
  public void testNodeNameRegexPattern() {

    final String expectedServerName = "WAGO61850Server";

    this.ensureMatches(
        "WAGO61850ServerWIND1/LLN0.Beh", expectedServerName, "1", "LLN0", null, "Beh");
    this.ensureMatches(
        "WAGO61850ServerWIND1/DRCC1.OutWSet", expectedServerName, "1", "DRCC", "1", "OutWSet");
    this.ensureMatches(
        "WAGO61850ServerWIND1/DGEN1.TotWh", expectedServerName, "1", "DGEN", "1", "TotWh");
    this.ensureMatches(
        "WAGO61850ServerWIND1/MMXU1.TotW", expectedServerName, "1", "MMXU", "1", "TotW");
    this.ensureMatches(
        "WAGO61850ServerWIND1/MMXU1.W.phsA", expectedServerName, "1", "MMXU", "1", "W.phsA");
    this.ensureMatches(
        "WAGO61850ServerWIND1/MMXU2.W.phsB", expectedServerName, "1", "MMXU", "2", "W.phsB");
    this.ensureMatches(
        "WAGO61850ServerWIND2/GGIO1.Alm1", expectedServerName, "2", "GGIO", "1", "Alm1");
    this.ensureMatches(
        "WAGO61850ServerWIND10/GGIO1.Alm1", expectedServerName, "10", "GGIO", "1", "Alm1");
  }

  private void ensureMatches(
      final String reference,
      final String expectedServerName,
      final String expectedSystemId,
      final String expectedNodeName,
      final String expectedNodeId,
      final String expectedAttribute) {

    final Matcher reportMatcher = NODE_PATTERN.matcher(reference);

    assertThat(reportMatcher.matches()).isTrue();

    assertThat(reportMatcher.group(1)).isEqualTo(expectedServerName);
    assertThat(reportMatcher.group(2)).isEqualTo(expectedSystemId);
    assertThat(reportMatcher.group(3)).isEqualTo(expectedNodeName);
    assertThat(reportMatcher.group(4)).isEqualTo(expectedNodeId);
    assertThat(reportMatcher.group(5)).isEqualTo(expectedAttribute);
  }
}
