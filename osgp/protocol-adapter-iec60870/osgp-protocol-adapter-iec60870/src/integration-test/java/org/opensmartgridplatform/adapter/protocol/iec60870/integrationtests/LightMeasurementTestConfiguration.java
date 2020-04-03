/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests;

import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.LightMeasurementClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.LightMeasurementService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.asduhandlers.InterrogationAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.lightmeasurement.asduhandlers.SinglePointWithQualityAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LightMeasurementTestConfiguration {
    @Bean
    public LightMeasurementClientAsduHandlerRegistry lightMeasurementClientASduHandlerRegistry() {
        return new LightMeasurementClientAsduHandlerRegistry();
    }

    @Bean
    public ClientAsduHandler lightMeasurementInterrogationCommandASduHandler() {
        return new InterrogationAsduHandler();
    }

    @Bean
    public ClientAsduHandler lightMeasurementSinglePointWithQualityAsduHandler() {
        return new SinglePointWithQualityAsduHandler();
    }

    @Bean
    public LightMeasurementService lightMeasurementService() {
        return new Iec60870LightMeasurementService();
    }
}
