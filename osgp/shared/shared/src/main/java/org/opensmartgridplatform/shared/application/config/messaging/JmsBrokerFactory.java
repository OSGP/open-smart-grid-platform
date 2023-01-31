/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.shared.application.config.messaging;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JmsBrokerFactory {
  private final Map<JmsBrokerType, JmsBroker> jmsBrokerMap;

  public JmsBrokerFactory(final JmsPropertyReader propertyReader) {
    this.jmsBrokerMap =
        Stream.of(new JmsBrokerActiveMq(propertyReader), new JmsBrokerArtemis(propertyReader))
            .collect(Collectors.toMap(JmsBroker::getBrokerType, Function.identity()));
  }

  public JmsBroker getBroker(final JmsBrokerType jmsBrokerType) {
    if (!this.jmsBrokerMap.containsKey(jmsBrokerType)) {
      throw new IllegalArgumentException("Unknown broker type: " + jmsBrokerType);
    }
    return this.jmsBrokerMap.get(jmsBrokerType);
  }
}
