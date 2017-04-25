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
import com.alliander.osgp.cucumber.platform.mocks.iec61850.Iec61850MockServer;

@ComponentScan(basePackages = {
        "com.alliander.osgp.cucumber" }, excludeFilters = @ComponentScan.Filter(value = MicrogridsNotificationWebServiceConfig.class, type = FilterType.ASSIGNABLE_TYPE))
public class ApplicationContext {

    @Bean(destroyMethod = "stop", initMethod = "start")
    Iec61850MockServer iec61850MockServerPampus() {
        return new Iec61850MockServer("PAMPUS", "Pampus_v0.4.5.icd", 62102, "WAGO61850Server");
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    Iec61850MockServer iec61850MockServerMarkerWadden() {
        return new Iec61850MockServer("MARKER WADDEN", "MarkerWadden_0_1_1.icd", 62103, "WAGO61850Server");

    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    Iec61850MockServer iec61850MockServerWago() {
        return new Iec61850MockServer("WAGO", "WAGO123.icd", 62104, "WAGO123");
    }
}
