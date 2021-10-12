/*
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.throttling.mapping;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import org.springframework.stereotype.Component;

@Component
public class ThrottlingMapper extends ConfigurableMapper {

  private static final String ID = "id";
  private static final String NAME = "name";
  private static final String MAX_CONCURRENCY = "maxConcurrency";

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
            .constructorA(ID, NAME, MAX_CONCURRENCY)
            .constructorB(NAME, MAX_CONCURRENCY) // a new entity does not get its ID from the API
            .fieldBToA(ID, ID) // entity ID is not updated from the API
            .fieldBToA(NAME, NAME) // name is not updated from the API
            .byDefault()
            .toClassMap());
  }
}
