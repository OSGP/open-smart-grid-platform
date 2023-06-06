// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest(showSql = false, excludeAutoConfiguration = FlywayAutoConfiguration.class)
@TestPropertySource(
    properties = {"spring.jpa.hibernate.ddl-auto=update", "spring.main.banner-mode=off"})
public abstract class AbstractRepositoryIT {
  @Autowired protected TestEntityManager entityManager;
}
