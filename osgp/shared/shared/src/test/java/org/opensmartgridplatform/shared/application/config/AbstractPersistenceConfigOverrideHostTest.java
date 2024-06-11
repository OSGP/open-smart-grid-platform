/*
 * Copyright 2023 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

package org.opensmartgridplatform.shared.application.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opensmartgridplatform.shared.infra.db.DefaultConnectionPoolFactory.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = OverrideHostPersistenceConfig.class)
class AbstractPersistenceConfigOverrideHostTest {
  @Autowired private ApplicationContext applicationContext;

  @Test
  void testOverrideHost() {
    final Builder singleHostBuilder = (Builder) this.applicationContext.getBean("testBuilder");
    final String connectionString = ReflectionTestUtils.getField(singleHostBuilder.build(), "databaseUrl").toString();
    assertThat(connectionString).isEqualTo("some:protocol://test-host:5432/a_database");
  }
}
