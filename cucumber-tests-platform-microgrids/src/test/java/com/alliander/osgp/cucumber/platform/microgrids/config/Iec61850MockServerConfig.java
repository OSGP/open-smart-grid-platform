/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.microgrids.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alliander.osgp.cucumber.platform.microgrids.mocks.iec61850.Iec61850MockServer;

@Configuration
public class Iec61850MockServerConfig {

    @Value("${iec61850.mock.networkaddress}")
    private String iec61850MockNetworkAddress;

    @Bean
    public String iec61850MockNetworkAddress() {
        return this.iec61850MockNetworkAddress;
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    public Iec61850MockServer iec61850MockServerPampus() {
        return new Iec61850MockServer("PAMPUS", "Pampus_v0.4.5.icd", 62102, "WAGO61850Server");
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    public Iec61850MockServer iec61850MockServerMarkerWadden() {
        return new Iec61850MockServer("MARKER WADDEN", "MarkerWadden_0_1_1_including_engine_control_logic.icd", 62103,
                "WAGO61850Server");
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    public Iec61850MockServer iec61850MockServerWago() {
        return new Iec61850MockServer("WAGO", "WAGO123.icd", 62104, "WAGO123");
    }

    @Bean(destroyMethod = "stop", initMethod = "start")
    public Iec61850MockServer iec61850MockServerPampusWithWindDevice() {
        return new Iec61850MockServer("PAMPUS_WITH_WIND", "Pampus_with_engine_profiles_and_wind.icd", 62105,
                "WAGO61850Server");
    }
}
