/**
 * Copyright 2021 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.simulator.protocol.iec60870.domain.defaultcontrolledstation;

import java.util.TimeZone;

import org.opensmartgridplatform.iec60870.Iec60870ConnectionRegistry;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduGenerator;
import org.opensmartgridplatform.simulator.protocol.iec60870.domain.Iec60870AsduGeneratorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.support.CronTrigger;

@Configuration
@ConditionalOnProperty("job.asdu.generator.enabled")
@Profile("default_controlled_station")
public class DefaultControlledStationConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultControlledStationConfig.class);

    @Value("${job.asdu.generator.cron:0 0/1 * * * ?}")
    private String cronExpression;

    @Bean
    public Iec60870AsduGenerator defaultControlledStationMeasurementAsduGenerator() {
        return new DefaultControlledStationMeasurementAsduGenerator();
    }

    /**
     * Bean used to generate measurement reports for testing purposes. By
     * default no measurement reports will be generated. To activate generation,
     * add job.asdu.generator.enabled to the application's properties.
     */
    @Bean
    @ConditionalOnProperty("job.asdu.generator.enabled")
    @Profile("default_controlled_station")
    public Iec60870AsduGeneratorService defaultControlledStationAsduGeneratorService(
            final Iec60870ConnectionRegistry iec60870ConnectionRegistry) {
        LOGGER.info("Default controlled station ASDU generator in simulator is enabled");

        final CronTrigger cronTrigger = new CronTrigger(this.cronExpression, TimeZone.getTimeZone("UTC"));
        final Iec60870AsduGenerator generator = this.defaultControlledStationMeasurementAsduGenerator();

        return new Iec60870AsduGeneratorService(iec60870ConnectionRegistry, cronTrigger, generator);
    }
}
