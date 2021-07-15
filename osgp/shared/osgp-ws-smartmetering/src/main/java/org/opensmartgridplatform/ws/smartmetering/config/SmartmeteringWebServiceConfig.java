/*
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.ws.smartmetering.config;

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

  private static final String COMMON_XSD_PATH = "schemas/common.xsd";
  private static final String SMART_METERING_INSTALLATION_XSD_PATH = "schemas/sm-installation.xsd";
  private static final String SMART_METERING_MANAGEMENT_XSD_PATH = "schemas/sm-management.xsd";
  private static final String SMART_METERING_BUNDLE_XSD_PATH = "schemas/sm-bundle.xsd";
  private static final String SMART_METERING_MONITORING_XSD_PATH = "schemas/sm-monitoring.xsd";
  private static final String SMART_METERING_ADHOC_XSD_PATH = "schemas/sm-adhoc.xsd";
  private static final String SMART_METERING_CONFIGURATION_XSD_PATH =
      "schemas/sm-configuration.xsd";

  private static final String SMART_METERING_INSTALLATION_WSDL_PATH =
      "SmartMeteringInstallation.wsdl";
  private static final String SMART_METERING_MANAGEMENT_WSDL_PATH = "SmartMeteringManagement.wsdl";
  private static final String SMART_METERING_BUNDLE_WSDL_PATH = "SmartMeteringBundle.wsdl";
  private static final String SMART_METERING_MONITORING_WSDL_PATH = "SmartMeteringMonitoring.wsdl";
  private static final String SMART_METERING_ADHOC_WSDL_PATH = "SmartMeteringAdhoc.wsdl";
  private static final String SMART_METERING_CONFIGURATION_WSDL_PATH =
      "SmartMeteringConfiguration.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH),
          new ClassPathResource(SMART_METERING_INSTALLATION_XSD_PATH),
          new ClassPathResource(SMART_METERING_MANAGEMENT_XSD_PATH),
          new ClassPathResource(SMART_METERING_BUNDLE_XSD_PATH),
          new ClassPathResource(SMART_METERING_MONITORING_XSD_PATH),
          new ClassPathResource(SMART_METERING_ADHOC_XSD_PATH),
          new ClassPathResource(SMART_METERING_CONFIGURATION_XSD_PATH)
        };
    payloadValidatingInterceptor.setSchemas(resources);
    return payloadValidatingInterceptor;
  }

  @Bean(name = "common")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "SmartMeteringInstallation")
  public WsdlDefinition smartMeteringInstallationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_INSTALLATION_WSDL_PATH));
  }

  @Bean(name = "sm-installation")
  public SimpleXsdSchema smartMeteringInstallationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_INSTALLATION_XSD_PATH));
  }

  @Bean(name = "SmartMeteringManagement")
  public WsdlDefinition smartMeteringManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "sm-management")
  public SimpleXsdSchema smartMeteringManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "SmartMeteringBundle")
  public WsdlDefinition smartMeteringBundleWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_BUNDLE_WSDL_PATH));
  }

  @Bean(name = "sm-bundle")
  public SimpleXsdSchema smartMeteringBundleXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_BUNDLE_XSD_PATH));
  }

  @Bean(name = "SmartMeteringMonitoring")
  public WsdlDefinition smartMeteringMonitoringWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_MONITORING_WSDL_PATH));
  }

  @Bean(name = "sm-monitoring")
  public SimpleXsdSchema smartMeteringMonitoringXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_MONITORING_XSD_PATH));
  }

  @Bean(name = "SmartMeteringAdhoc")
  public WsdlDefinition smartMeteringAdhocWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_ADHOC_WSDL_PATH));
  }

  @Bean(name = "sm-adhoc")
  public SimpleXsdSchema smartMeteringAdhocXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_ADHOC_XSD_PATH));
  }

  @Bean(name = "SmartMeteringConfiguration")
  public WsdlDefinition smartMeteringConfigurationWsdl() {
    return new SimpleWsdl11Definition(
        new ClassPathResource(SMART_METERING_CONFIGURATION_WSDL_PATH));
  }

  @Bean(name = "sm-configuration")
  public SimpleXsdSchema smartMeteringConfigurationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_CONFIGURATION_XSD_PATH));
  }
}
