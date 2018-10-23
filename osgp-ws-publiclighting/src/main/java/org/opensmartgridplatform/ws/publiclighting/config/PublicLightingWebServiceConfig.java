/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.ws.publiclighting.config;

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

    private static final String COMMON_XSD_PATH = "schemas/common.xsd";
    private static final String DEVICE_MONITORING_XSD_PATH = "schemas/devicemonitoring.xsd";
    private static final String PL_ADHOCMANAGEMENT_XSD_PATH = "schemas/pl-adhocmanagement.xsd";
    private static final String PL_SCHEDULEMANAGEMENT_XSD_PATH = "schemas/pl-schedulemanagement.xsd";

    private static final String DEVICE_MONITORING_WSDL_PATH = "DeviceMonitoring.wsdl";
    private static final String PL_ADHOC_MANAGEMENT_WSDL_PATH = "PublicLightingAdHocManagement.wsdl";
    private static final String PL_SCHEDULE_MANAGEMENT_WSDL_PATH = "PublicLightingScheduleManagement.wsdl";

    @Bean
    public PayloadValidatingInterceptor payloadValidatingInterceptor() {
        final PayloadValidatingInterceptor payloadValidatingInterceptor = new PayloadValidatingInterceptor();
        final Resource[] resources = new Resource[] { new ClassPathResource(COMMON_XSD_PATH),
                new ClassPathResource(DEVICE_MONITORING_XSD_PATH), new ClassPathResource(PL_ADHOCMANAGEMENT_XSD_PATH),
                new ClassPathResource(PL_SCHEDULEMANAGEMENT_XSD_PATH) };
        payloadValidatingInterceptor.setSchemas(resources);
        return payloadValidatingInterceptor;
    }

    @Bean(name = "common")
    public SimpleXsdSchema commonXsd() {
        return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
    }

    @Bean(name = "DeviceMonitoring")
    public WsdlDefinition deviceMonitoringWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_MONITORING_WSDL_PATH));
    }

    @Bean(name = "devicemonitoring")
    public SimpleXsdSchema deviceMonitoringXsd() {
        return new SimpleXsdSchema(new ClassPathResource(DEVICE_MONITORING_XSD_PATH));
    }

    @Bean(name = "PublicLightingAdHocManagement")
    public WsdlDefinition publicLightingAdHocManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource(PL_ADHOC_MANAGEMENT_WSDL_PATH));
    }

    @Bean(name = "pl-adhocmanagement")
    public SimpleXsdSchema publicLightingAdHocManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource(PL_ADHOCMANAGEMENT_XSD_PATH));
    }

    @Bean(name = "PublicLightingScheduleManagement")
    public WsdlDefinition publicLightingScheduleManagementWsdl() {
        return new SimpleWsdl11Definition(new ClassPathResource(PL_SCHEDULE_MANAGEMENT_WSDL_PATH));
    }

    @Bean(name = "pl-schedulemanagement")
    public SimpleXsdSchema publicLightingScheduleManagementXsd() {
        return new SimpleXsdSchema(new ClassPathResource(PL_SCHEDULEMANAGEMENT_XSD_PATH));
    }
}
