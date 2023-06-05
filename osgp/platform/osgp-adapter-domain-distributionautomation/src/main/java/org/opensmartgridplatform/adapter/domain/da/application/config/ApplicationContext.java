// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.domain.da.application.config;

import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java configuration requires Spring
 * Framework 3.0
 */
@Configuration
@ComponentScan("org.opensmartgridplatform.shared.domain.services")
@ComponentScan("org.opensmartgridplatform.domain.core")
@ComponentScan("org.opensmartgridplatform.adapter.domain.da")
@PropertySource("classpath:osgp-adapter-domain-distributionautomation.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
@PropertySource(
    value = "file:${osgp/AdapterDomainDistributionAutomation/config}",
    ignoreResourceNotFound = true)
@Import({
  MessagingConfig.class,
  OsgpSchedulerConfig.class,
  PersistenceConfig.class,
  MetricsConfig.class
})
@EnableTransactionManagement
public class ApplicationContext {}
