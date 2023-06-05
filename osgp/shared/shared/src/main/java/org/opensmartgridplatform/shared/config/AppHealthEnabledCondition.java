// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
