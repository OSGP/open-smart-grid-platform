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

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class AppHealthEnabledCondition implements Condition {

  @Override
  public boolean matches(
      final ConditionContext context, final @NotNull AnnotatedTypeMetadata metadata) {
    return "true".equalsIgnoreCase(context.getEnvironment().getProperty("healthcheck.enabled"));
  }
}
