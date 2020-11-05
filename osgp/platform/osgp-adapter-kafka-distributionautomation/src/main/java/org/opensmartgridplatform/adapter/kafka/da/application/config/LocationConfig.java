/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.kafka.da.application.config;

import javax.annotation.Resource;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:location-info.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
public class LocationConfig {

    @Resource
    private Environment environment;

    public String getSubstationLocation(final String substationIdentification) {
        return this.environment.getRequiredProperty(substationIdentification + ".location");
    }

    public String getBayIdentification(final String substationIdentification, final String feeder) {
        return this.environment.getRequiredProperty(substationIdentification + ".feeder." + feeder);
    }
}
