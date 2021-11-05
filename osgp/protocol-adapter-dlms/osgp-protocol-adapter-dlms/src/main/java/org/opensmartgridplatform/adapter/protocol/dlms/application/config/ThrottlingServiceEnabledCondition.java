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
