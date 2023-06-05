// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.protocol.dlms.application.config;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class ThrottlingServiceEnabledCondition extends ThrottlingClientEnabledCondition {

  @Override
  public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
    // Either the ThrottlingClient or the ThrottlingService should be used for throttling.
    // So, this should behave as the negation of matches in ThrottlingClientEnabledCondition.
    return !super.matches(context, metadata);
  }
}
