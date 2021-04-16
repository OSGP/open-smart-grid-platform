/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.domain.da.application.config;

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
@Import({MessagingConfig.class, OsgpSchedulerConfig.class, PersistenceConfig.class})
@EnableTransactionManagement
public class ApplicationContext {}
