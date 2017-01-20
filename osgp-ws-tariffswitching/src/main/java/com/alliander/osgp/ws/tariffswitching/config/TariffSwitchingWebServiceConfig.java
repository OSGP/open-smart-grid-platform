/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.ws.tariffswitching.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class TariffSwitchingWebServiceConfig {

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        final Resource[] resources = new Resource[] { new ClassPathResource("schemas/common.xsd"),
                new ClassPathResource("schemas/ts-adhocmanagment.xsd"),
                new ClassPathResource("schemas/ts-schedulemanagement.xsd") };
        payloadValidatingInterceptor.setSchemas(resources);
        return payloadValidatingInterceptor;
    }

    @Bean(name = "common")
    public SimpleXsdSchema commonXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/common.xsd"));
    }

    @Bean(name = "TariffSwitchingAdHocManagment")
    public WsdlDefinition tariffSwitchingAdHocManagmentWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("TariffSwitchingAdHocManagment.wsdl"));
    }

    @Bean(name = "ts-adhocmanagement")
    public SimpleXsdSchema tariffSwitchingAdHocManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/ts-adhocmanagement.xsd"));
    }

    @Bean(name = "TariffSwitchingScheduleManagment")
    public WsdlDefinition tariffSwitchingScheduleManagmentWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("TariffSwitchingScheduleManagment.wsdl"));
    }

    @Bean(name = "ts-schedulemanagement")
    public SimpleXsdSchema tariffSwitchingScheduleManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/ts-schedulemanagement.xsd"));
    }
}
