/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.dbsupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.alliander.osgp.platform.cucumber.ApplicationConfig;

/**
 * An application context Java configuration class. The usage of Java
 * configuration requires Spring Framework 3.0
 */
@ComponentScan(basePackages = { "com.alliander.osgp.adapter.ws.smartmetering.domain.entities",
        "com.alliander.osgp.domain.core.repositories", "com.alliander.osgp.domain.core.entities",
        "com.alliander.osgp.logging.domain.repositories","com.alliander.osgp.platform.cucumber.hooks",
        "com.alliander.osgp.platform.cucumber" })
@EnableTransactionManagement()
@Import({ PersistenceConfigCore.class, PersistenceConfigResponseData.class, PersistenceConfigResponseDlms.class,
    PersistenceConfigLogging.class})
@Component
public class ApplicationContext {

    @Autowired
    private ApplicationConfig applicationConfig;
}
