/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.ws.smartmetering.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class SmartmeteringWebServiceConfig {

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        final Resource[] resources = new Resource[] { new ClassPathResource("schemas/common.xsd"),
                new ClassPathResource("schemas/sm-installation.xsd"),
                new ClassPathResource("schemas/sm-management.xsd"), new ClassPathResource("schemas/sm-bundle.xsd"),
                new ClassPathResource("schemas/sm-monitoring.xsd"), new ClassPathResource("schemas/sm-adhoc.xsd"),
                new ClassPathResource("schemas/sm-configuration.xsd") };
        payloadValidatingInterceptor.setSchemas(resources);
        return payloadValidatingInterceptor;
    }

    @Bean(name = "common")
    public SimpleXsdSchema commonXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/common.xsd"));
    }

    @Bean(name = "SmartMeteringInstallation")
    public WsdlDefinition smartMeteringInstallationWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringInstallation.wsdl"));
    }

    @Bean(name = "sm-installation")
    public SimpleXsdSchema smartMeteringInstallationXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-installation.xsd"));
    }

    @Bean(name = "SmartMeteringManagement")
    public WsdlDefinition smartMeteringManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringManagement.wsdl"));
    }

    @Bean(name = "sm-management")
    public SimpleXsdSchema smartMeteringManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-management.xsd"));
    }

    @Bean(name = "SmartMeteringBundle")
    public WsdlDefinition smartMeteringBundleWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringBundle.wsdl"));
    }

    @Bean(name = "sm-bundle")
    public SimpleXsdSchema smartMeteringBundleXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-bundle.xsd"));
    }

    @Bean(name = "SmartMeteringMonitoring")
    public WsdlDefinition smartMeteringMonitoringWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringMonitoring.wsdl"));
    }

    @Bean(name = "sm-monitoring")
    public SimpleXsdSchema smartMeteringMonitoringXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-monitoring.xsd"));
    }

    @Bean(name = "SmartMeteringAdhoc")
    public WsdlDefinition smartMeteringAdhocWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringAdhoc.wsdl"));
    }

    @Bean(name = "sm-adhoc")
    public SimpleXsdSchema smartMeteringAdhocXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-adhoc.xsd"));
    }

    @Bean(name = "SmartMeteringConfiguration")
    public WsdlDefinition smartMeteringConfigurationWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringConfiguration.wsdl"));
    }

    @Bean(name = "sm-configuration")
    public SimpleXsdSchema smartMeteringConfigurationXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-configuration.xsd"));
    }

    @Bean(name = "SmartMeteringNotification")
    public WsdlDefinition smartMeteringNotificationWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource("SmartMeteringNotification.wsdl"));
    }

    @Bean(name = "sm-notification")
    public SimpleXsdSchema smartMeteringNotificationXsd() {
        return new SimpleXsdSchema(new ClassPathResource("schemas/sm-notification.xsd"));
    }
}
