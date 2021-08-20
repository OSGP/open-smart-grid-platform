/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.mqtt.application.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MqttClientSslKeystoreEnabledCondition implements Condition {

  @Override
  public boolean matches(final ConditionContext context, final AnnotatedTypeMetadata metadata) {
    return "true".equalsIgnoreCase(context.getEnvironment().getProperty("mqtt.client.ssl.enabled"))
        && context.getEnvironment().getProperty("mqtt.client.ssl.truststore.location") != null;
  }
}
