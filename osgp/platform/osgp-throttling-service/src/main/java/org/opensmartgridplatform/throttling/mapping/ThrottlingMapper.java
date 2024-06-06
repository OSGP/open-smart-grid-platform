// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.throttling.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class ThrottlingMapper extends ConfigurableMapper {

  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String MAX_CONCURRENCY = "maxConcurrency";
  private static final String MAX_NEW_CONNECTIONS = "maxNewConnections";
  private static final String MAX_NEW_CONNECTIONS_RESET_TIME_IN_MS =
      "maxNewConnectionsResetTimeInMs";
  private static final String MAX_NEW_CONNECTIONS_WAIT_TIME_IN_MS = "maxNewConnectionsWaitTimeInMs";

  @Override
  protected void configure(final MapperFactory factory) {
    this.registerThrottlingConfigMapping(factory);
  }

  private void registerThrottlingConfigMapping(final MapperFactory factory) {
    factory.registerClassMap(
        factory
            .classMap(
                org.opensmartgridplatform.throttling.api.ThrottlingConfig.class,
                org.opensmartgridplatform.throttling.entities.ThrottlingConfig.class)
            .constructorA(
                ID,
                NAME,
                MAX_CONCURRENCY,
                MAX_NEW_CONNECTIONS,
                MAX_NEW_CONNECTIONS_RESET_TIME_IN_MS,
                MAX_NEW_CONNECTIONS_WAIT_TIME_IN_MS)
            .constructorB(
                NAME,
                MAX_CONCURRENCY,
                MAX_NEW_CONNECTIONS,
                MAX_NEW_CONNECTIONS_RESET_TIME_IN_MS,
                MAX_NEW_CONNECTIONS_WAIT_TIME_IN_MS) // a new entity does not get its ID from the
            // API
            .fieldBToA(ID, ID) // entity ID is not updated from the API
            .fieldBToA(NAME, NAME) // name is not updated from the API
            .byDefault()
            .toClassMap());
  }
}
