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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource("classpath:location-info.properties")
@PropertySource(value = "file:${osgp/Global/config}", ignoreResourceNotFound = true)
public class LocationConfig {

    @Resource
    private Environment environment;

    public String getSubstationLocation(final String substationIdentification) {
        final String property = substationIdentification + ".location";
        return this.readProperty(property);
    }

    public String getBayIdentification(final String substationIdentification, final String feeder) {
        final String property = substationIdentification + ".feeder." + feeder;
        return this.readProperty(property);
    }

    private String readProperty(final String property) {
        try {
            return this.environment.getRequiredProperty(property);
        } catch (final IllegalStateException e) {
            log.error("Property {} not found, returning an empty string", property, e);
            return "";
        }
    }

}
