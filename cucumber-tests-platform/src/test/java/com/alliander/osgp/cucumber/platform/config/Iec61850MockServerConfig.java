/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Iec61850MockServerConfig {

    @Value("${iec61850.mock.networkaddress}")
    private String iec61850MockNetworkAddress;
    @Value("${iec61850.mock.icd.filename}")
    private String iec61850MockIcdFilename;
    @Value("${iec61850.mock.port}")
    private int iec61850MockPort;

    @Bean
    public String iec61850MockNetworkAddress() {
        return this.iec61850MockNetworkAddress;
    }

    @Bean
    public int iec61850MockPort() {
        return this.iec61850MockPort;
    }

    @Bean
    public String iec61850MockIcdFilename() {
        return this.iec61850MockIcdFilename;
    }
}
