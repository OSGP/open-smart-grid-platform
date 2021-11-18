/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.opensmartgridplatform.throttling.ThrottlingClient;
import org.springframework.context.annotation.Lazy;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.ResourceAccessException;

class ThrottlingClientConfigTest {
  private final ThrottlingClientConfig throttlingClientConfig = new ThrottlingClientConfig();

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(
        this.throttlingClientConfig, "throttlingServiceUrl", "http://localhost:9090");
    ReflectionTestUtils.setField(this.throttlingClientConfig, "timeout", Duration.ofMillis(1));
    ReflectionTestUtils.setField(this.throttlingClientConfig, "delay", Duration.ofMillis(2));
  }

  @Test
  void testShouldHaveLazyAnnotation() throws NoSuchMethodException {
    final Method throttlingClientMethod =
        ThrottlingClientConfig.class.getDeclaredMethod("throttlingClient", null);
    final Annotation[] annotations = throttlingClientMethod.getDeclaredAnnotations();
    final Optional<Annotation> lazyAnnotation =
        Arrays.stream(annotations)
            .filter(annotation -> annotation.annotationType().isAssignableFrom(Lazy.class))
            .findFirst();
    assertThat(lazyAnnotation).isPresent();
  }

  @Test
  void registerThrottlingClient() throws URISyntaxException {
    final ThrottlingClient throttlingClient = mock(ThrottlingClient.class);

    this.throttlingClientConfig.registerThrottlingClient(throttlingClient);
    verify(throttlingClient).register();
  }

  @Test
  void registerThrottlingClientRetry() throws URISyntaxException {
    final ThrottlingClient throttlingClient = mock(ThrottlingClient.class);

    doThrow(new ResourceAccessException("")).doNothing().when(throttlingClient).register();

    this.throttlingClientConfig.registerThrottlingClient(throttlingClient);
    verify(throttlingClient, times(2)).register();
  }
}
