/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.iec60870.integrationtests;

import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.application.services.Iec60870MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.distributionautomation.DistributionAutomationClientAsduHandlerRegistry;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.distributionautomation.MeasurementReportingService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.distributionautomation.asduhandlers.InterrogationAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.distributionautomation.asduhandlers.ShortFloatWithTime56MeasurementAsduHandler;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.AsduConverterService;
import org.opensmartgridplatform.adapter.protocol.iec60870.domain.services.ClientAsduHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DistributionAutomationTestConfiguration {

    @Bean
    public DistributionAutomationClientAsduHandlerRegistry distributionAutomationClientASduHandlerRegistry() {
        return new DistributionAutomationClientAsduHandlerRegistry();
    }

    @Bean
    public ClientAsduHandler shortFloatWithTime56MeasurementASduHandler() {
        return new ShortFloatWithTime56MeasurementAsduHandler();
    }

    @Bean
    public ClientAsduHandler distributionAutomationInterrogationCommandASduHandler() {
        return new InterrogationAsduHandler();
    }

    @Bean
    public MeasurementReportingService measurementReportMessageSender() {
        return new Iec60870MeasurementReportingService();
    }

    @Bean
    public AsduConverterService asduToMeasurementReportMapper() {
        return new Iec60870AsduConverterService();
    }

}
