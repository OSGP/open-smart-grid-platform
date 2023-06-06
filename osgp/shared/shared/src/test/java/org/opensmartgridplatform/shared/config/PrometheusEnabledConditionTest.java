// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.shared.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.mock.env.MockEnvironment;

@ExtendWith(MockitoExtension.class)
class PrometheusEnabledConditionTest {
  private final PrometheusEnabledCondition prometheusEnabledCondition =
      new PrometheusEnabledCondition();
  @Mock private ConditionContext conditionContext;

  @Test
  void shouldBeTrueOnTrueProperty() {
    final MockEnvironment mockEnvironment =
        new MockEnvironment().withProperty("metrics.prometheus.enabled", "true");
    when(this.conditionContext.getEnvironment()).thenReturn(mockEnvironment);

    assertThat(this.prometheusEnabledCondition.matches(this.conditionContext, null)).isTrue();
  }

  @Test
  void shouldBeFalseOnMissingProperty() {
    final MockEnvironment mockEnvironment = new MockEnvironment();
    when(this.conditionContext.getEnvironment()).thenReturn(mockEnvironment);

    assertThat(this.prometheusEnabledCondition.matches(this.conditionContext, null)).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "no", "yes", "banana", ""})
  void shouldBeFalseOnPropertyOtherThanTrueProperty(final String propertyValue) {
    final MockEnvironment mockEnvironment =
        new MockEnvironment().withProperty("metrics.prometheus.enabled", propertyValue);
    when(this.conditionContext.getEnvironment()).thenReturn(mockEnvironment);

    assertThat(this.prometheusEnabledCondition.matches(this.conditionContext, null)).isFalse();
  }
}
