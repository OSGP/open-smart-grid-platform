/*
 * SPDX-FileCopyrightText: Copyright Contributors to the GXF project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensmartgridplatform.adapter.protocol.dlms.application.config.annotation;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public abstract class ThrottlingTypeCondition implements Condition {

  private final String throttlingType;

  public ThrottlingTypeCondition(final String throttlingType) {
    this.throttlingType = throttlingType;
  }

  @Override
  public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
    return this.throttlingType.equalsIgnoreCase(
        context.getEnvironment().getProperty("throttling.type"));
  }
}
