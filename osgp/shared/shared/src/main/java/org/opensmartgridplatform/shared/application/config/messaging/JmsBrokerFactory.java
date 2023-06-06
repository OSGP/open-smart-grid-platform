// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

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
