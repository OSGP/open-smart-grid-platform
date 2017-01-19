/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.ws.admin.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class AdminWebServiceConfig {

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        final Resource[] resources = new Resource[] { new ClassPathResource("schemas/common.xsd"),
                new ClassPathResource("schemas/devicemanagement.xsd"), };
        payloadValidatingInterceptor.setSchemas(resources);
        return payloadValidatingInterceptor;
    }

    @Bean(name = "common")
    public SimpleXsdSchema commonXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/common.xsd"));
    }

    @Bean(name = "DeviceManagement")
    public WsdlDefinition deviceManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("DeviceManagement.wsdl"));
    }

    @Bean(name = "devicemanagement")
    public SimpleXsdSchema deviceManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/devicemanagement.xsd"));
    }

}
