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

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MultipleHostPersistnceConfig.class)
class AbstractPersistenceConfigMultipleHostsTest {
  @Autowired private ApplicationContext applicationContext;

  @Test
  void testMultipleDatabaseHost() {
    final Builder multipleHostsBuilder =
        (Builder) this.applicationContext.getBean("multipleHostsBuilder");
    final String connectionString = multipleHostsBuilder.build().getDatabaseConnectionString();
    assertThat(connectionString)
        .isEqualTo("multiple://firstHost:1111,secondHost:2222,thirdHost:3333/multiple_database");
  }
}
