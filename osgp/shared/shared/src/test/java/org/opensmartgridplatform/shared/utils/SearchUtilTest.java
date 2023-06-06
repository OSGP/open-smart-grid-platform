// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class SearchUtilTest {

  @Test
  void normalInput() {
    final String input = "normal input without wildcards";
    assertThat(SearchUtil.replaceWildcards(input)).isEqualTo(input);
    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo(input);
  }

  @Test
  public void escapePercentageCharacters() {
    final String input = "TST%-111%";

    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo("TST\\%-111\\%");
  }

  @Test
  public void escapeUnderscoreCharacter() {
    final String input = "TST-1_1";

    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo("TST-1\\_1");
  }

  @Test
  void escapeBothWildcards() {

    final String input = "input with_ %wildcards";
    final String expected = "input with\\_ \\%wildcards";

    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo(expected);
  }

  @Test
  public void convertAsteriskToPercentage() {
    final String input = "TST-11*";

    assertThat(SearchUtil.replaceWildcards(input)).isEqualTo("TST-11%");
    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo("TST-11%");
  }

  @Test
  public void convertQuestionMarksToUnderscores() {
    final String input = "TS???-111";

    assertThat(SearchUtil.replaceWildcards(input)).isEqualTo("TS___-111");
    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo("TS___-111");
  }

  @Test
  public void convertNull() {
    final String input = null;

    assertThat(SearchUtil.replaceWildcards(input)).isNull();
    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isNull();
  }

  @Test
  public void escapeBackslashes() {
    final String input = "TS\\T-1\\11";

    assertThat(SearchUtil.replaceAndEscapeWildcards(input)).isEqualTo("TS\\\\T-1\\\\11");
  }
}
