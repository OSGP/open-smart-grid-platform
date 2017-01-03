/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * An application context Java configuration class.
 */
@Configuration
@ComponentScan(basePackages = { "com.alliander.osgp.adapter.ws.smartmetering.domain.entities",
        "com.alliander.osgp.domain.core.repositories", "com.alliander.osgp.domain.core.entities",
        "com.alliander.osgp.domain.microgrids.repositories", "com.alliander.osgp.domain.microgrids.entities",
        "com.alliander.osgp.logging.domain.repositories",
        "com.alliander.osgp.adapter.protocol.iec61850.domain.repositories",
        "com.alliander.osgp.adapter.protocol.iec61850.domain.entities",
        "com.alliander.osgp.adapter.protocol.oslp.domain.repositories",
        "com.alliander.osgp.adapter.protocol.oslp.domain.entities", "com.alliander.osgp.platform.cucumber.hooks" })
@EnableTransactionManagement()
@Import({ CorePersistenceConfig.class, AdapterWsSmartMeteringPersistenceConfig.class,
        AdapterProtocolDlmsPersistenceConfig.class, LoggingPersistenceConfig.class,
        AdapterProtocolIec61850PersistenceConfig.class, AdapterProtocolOslpPersistenceConfig.class,
        AdapterWsMicrogridsPersistenceConfig.class, CoreDeviceConfiguration.class, Iec61850MockServerConfig.class })
public class ApplicationContext {

}
