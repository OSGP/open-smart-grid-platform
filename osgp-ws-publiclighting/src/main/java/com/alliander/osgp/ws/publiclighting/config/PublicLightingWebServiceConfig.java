/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.ws.publiclighting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class PublicLightingWebServiceConfig {

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        final Resource[] resources = new Resource[] { new ClassPathResource("schemas/common.xsd"),
                new ClassPathResource("schemas/devicemonitoring.xsd"),
                new ClassPathResource("schemas/pl-adhocmanagement.xsd"),
                new ClassPathResource("schemas/pl-schedulemanagement.xsd") };
        payloadValidatingInterceptor.setSchemas(resources);
        return payloadValidatingInterceptor;
    }

    @Bean(name = "common")
    public SimpleXsdSchema commonXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/common.xsd"));
    }

    @Bean(name = "DeviceMonitoring")
    public WsdlDefinition deviceMonitoringWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("DeviceMonitoring.wsdl"));
    }

    @Bean(name = "devicemonitoring")
    public SimpleXsdSchema deviceMonitoringXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/devicemonitoring.xsd"));
    }

    @Bean(name = "PublicLightingAdHocManagement")
    public WsdlDefinition publicLightingAdHocManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("PublicLightingAdHocManagement.wsdl"));
    }

    @Bean(name = "pl-adhocmanagement")
    public SimpleXsdSchema publicLightingAdHocManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/pl-adhocmanagement.xsd"));
    }

    @Bean(name = "PublicLightingScheduleManagement")
    public WsdlDefinition publicLightingScheduleManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("PublicLightingScheduleManagement.wsdl"));
    }

    @Bean(name = "pl-schedulemanagement")
    public SimpleXsdSchema publicLightingScheduleManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/pl-schedulemanagement.xsd"));
    }
}
