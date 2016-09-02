/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.dbsupport;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.adapter.ws.smartmetering.domain.entities",
        "com.alliander.osgp.domain.core.repositories", "com.alliander.osgp.domain.core.entities",
"com.alliander.osgp.logging.domain.repositories" })
@EnableTransactionManagement()
@Import({ PersistenceConfigCore.class, PersistenceConfigResponseData.class, PersistenceConfigResponseDlms.class,
    PersistenceConfigLogging.class })
@PropertySource("file:/etc/osp/osgp-cucumber.properties")
public class ApplicationContext {
}
