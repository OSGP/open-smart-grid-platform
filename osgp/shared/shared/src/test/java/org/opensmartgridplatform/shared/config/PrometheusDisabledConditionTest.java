/*
 * Copyright 2022 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

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
class PrometheusDisabledConditionTest {
  private final PrometheusDisabledCondition prometheusDisabledCondition =
      new PrometheusDisabledCondition();
  @Mock private ConditionContext conditionContext;

  @Test
  void shouldBeTrueOnMissingProperty() {
    final MockEnvironment mockEnvironment = new MockEnvironment();
    when(this.conditionContext.getEnvironment()).thenReturn(mockEnvironment);

    assertThat(this.prometheusDisabledCondition.matches(this.conditionContext, null)).isTrue();
  }

  @ParameterizedTest
  @ValueSource(strings = {"false", "no", "yes", "banana", ""})
  void shoudBeTrueOnPropertyOtherThanTrueProperty(final String propertyValue) {
    final MockEnvironment mockEnvironment =
        new MockEnvironment().withProperty("metrics.prometheus.enabled", propertyValue);
    when(this.conditionContext.getEnvironment()).thenReturn(mockEnvironment);

    assertThat(this.prometheusDisabledCondition.matches(this.conditionContext, null)).isTrue();
  }

  @Test
  void shoudBeFalseOnTrueProperty() {
    final MockEnvironment mockEnvironment =
        new MockEnvironment().withProperty("metrics.prometheus.enabled", "true");
    when(this.conditionContext.getEnvironment()).thenReturn(mockEnvironment);

    assertThat(this.prometheusDisabledCondition.matches(this.conditionContext, null)).isFalse();
  }
}
