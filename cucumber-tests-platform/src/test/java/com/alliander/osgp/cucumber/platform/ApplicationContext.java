/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import com.alliander.osgp.cucumber.platform.config.ws.microgrids.MicrogridsNotificationWebServiceConfig;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServerBase;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServerMarkerWadden;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServerPampus;
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServerWago;

//@Configuration
@ComponentScan(basePackages = {
        "com.alliander.osgp.cucumber" }, excludeFilters = @ComponentScan.Filter(value = MicrogridsNotificationWebServiceConfig.class, type = FilterType.ASSIGNABLE_TYPE))
public class ApplicationContext {

    @Bean(destroyMethod = "stop", initMethod = "start")
    Iec61850MockServerBase iec61850MockServerPampus() {
        return new Iec61850MockServerPampus();
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    Iec61850MockServerBase iec61850MockServerMarkerWadden() {
        return new Iec61850MockServerMarkerWadden();
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    Iec61850MockServerBase iec61850MockServerWago() {
        return new Iec61850MockServerWago();
    }
}
