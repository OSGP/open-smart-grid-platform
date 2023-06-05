// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.metrics;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

class MetricsNameServiceTest {
  private static final String METRIC_NAME = "some.test.metrics";

  @ParameterizedTest
  @MethodSource("blankStrings")
  void returnsSameNameWhenNoApplicationNameIsConfigured(final String blankApplicationName) {
    final TestableMetricsNameService metricsService =
        this.createMetricsService(blankApplicationName);
    assertThat(metricsService.createName(METRIC_NAME)).isEqualTo(METRIC_NAME);
  }

  @Test
  void returnsNameWithNewApplicationPrefixIfNotAlreadyPresent() {
    final String applicationName = "testapp";
    final TestableMetricsNameService metricsService = this.createMetricsService(applicationName);
    assertThat(metricsService.createName(METRIC_NAME))
        .isEqualTo(applicationName + "." + METRIC_NAME);
  }

  @Test
  void returnsSameNameWhenApplicationnameIsAlreadyPresent() {
    final String applicationName = METRIC_NAME.split("\\.")[0]; // "some"
    final TestableMetricsNameService metricsService = this.createMetricsService(applicationName);
    assertThat(metricsService.createName(METRIC_NAME)).isEqualTo(METRIC_NAME);
  }

  static Stream<String> blankStrings() {
    return Stream.of("", "   ", null);
  }

  private TestableMetricsNameService createMetricsService(final String applicationName) {
    final TestableMetricsNameService metricsService = new TestableMetricsNameService();
    ReflectionTestUtils.setField(metricsService, "applicationName", applicationName);
    return metricsService;
  }

  private static class TestableMetricsNameService extends MetricsNameService {}
}
