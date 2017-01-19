package com.alliander.osgp.ws.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */

@Configuration
public class CoreWebServiceConfig {

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        final Resource[] resources = new Resource[] { new ClassPathResource("schemas/common.xsd"),
                new ClassPathResource("schemas/adhocmanagement.xsd"),
                new ClassPathResource("schemas/configurationmanagement.xsd"),
                new ClassPathResource("schemas/deviceinstallation.xsd"),
                new ClassPathResource("schemas/devicemanagement.xsd"),
                new ClassPathResource("schemas/firmwaremanagement.xsd") };
        payloadValidatingInterceptor.setSchemas(resources);
        return payloadValidatingInterceptor;
    }

    @Bean(name = "common")
    public SimpleXsdSchema commonXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/common.xsd"));
    }

    @Bean(name = "AdHocManagement")
    public WsdlDefinition AdHocManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("AdHocManagement.wsdl"));
    }

    @Bean(name = "adhocmanagement")
    public SimpleXsdSchema adhocManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/adhocmanagement.xsd"));
    }

    @Bean(name = "ConfigurationManagement")
    public WsdlDefinition configurationManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("ConfigurationManagement.wsdl"));
    }

    @Bean(name = "configurationmanagement")
    public SimpleXsdSchema configurationManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/configurationmanagement.xsd"));
    }

    @Bean(name = "DeviceInstallation")
    public WsdlDefinition deviceInstallationWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("DeviceInstallation.wsdl"));
    }

    @Bean(name = "deviceinstallation")
    public SimpleXsdSchema deviceInstallationXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/deviceinstallation.xsd"));
    }

    @Bean(name = "DeviceManagement")
    public WsdlDefinition deviceManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("DeviceManagement.wsdl"));
    }

    @Bean(name = "devicemanagement")
    public SimpleXsdSchema deviceManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/devicemanagement.xsd"));
    }

    @Bean(name = "FirmwareManagement")
    public WsdlDefinition firmwareManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("FirmwareManagement.wsdl"));
    }

    @Bean(name = "firmwaremanagement")
    public SimpleXsdSchema firmwareManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/firmwaremanagement.xsd"));
    }
}
