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

  private static final String COMMON_XSD_PATH = "schemas/base-ws-smartmetering.xsd";
  private static final String SMART_METERING_COMMON_XSD_PATH =
      "schemas/common-ws-smartmetering.xsd";
  private static final String SMART_METERING_INSTALLATION_XSD_PATH =
      "schemas/installation-ws-smartmetering.xsd";
  private static final String SMART_METERING_MANAGEMENT_XSD_PATH =
      "schemas/management-ws-smartmetering.xsd";
  private static final String SMART_METERING_BUNDLE_XSD_PATH =
      "schemas/bundle-ws-smartmetering.xsd";
  private static final String SMART_METERING_MONITORING_XSD_PATH =
      "schemas/monitoring-ws-smartmetering.xsd";
  private static final String SMART_METERING_ADHOC_XSD_PATH = "schemas/adhoc-ws-smartmetering.xsd";
  private static final String SMART_METERING_CONFIGURATION_XSD_PATH =
      "schemas/configuration-ws-smartmetering.xsd";

  private static final String SMART_METERING_COMMON_WSDL_PATH = "SmartMeteringCommon.wsdl";
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
          new ClassPathResource(SMART_METERING_COMMON_XSD_PATH),
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

  @Bean(name = "base-ws-smartmetering")
  public SimpleXsdSchema baseWsSmartmeteringXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "common")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "SmartMeteringCommon")
  public WsdlDefinition smartMeteringCommonWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_COMMON_WSDL_PATH));
  }

  @Bean(name = "common-ws-smartmetering")
  public SimpleXsdSchema smartMeteringCommonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_COMMON_XSD_PATH));
  }

  @Bean(name = "SmartMeteringInstallation")
  public WsdlDefinition smartMeteringInstallationWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_INSTALLATION_WSDL_PATH));
  }

  @Bean(name = "installation-ws-smartmetering")
  public SimpleXsdSchema smartMeteringInstallationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_INSTALLATION_XSD_PATH));
  }

  @Bean(name = "SmartMeteringManagement")
  public WsdlDefinition smartMeteringManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "management-ws-smartmetering")
  public SimpleXsdSchema smartMeteringManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_MANAGEMENT_XSD_PATH));
  }

  @Bean(name = "SmartMeteringBundle")
  public WsdlDefinition smartMeteringBundleWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_BUNDLE_WSDL_PATH));
  }

  @Bean(name = "bundle-ws-smartmetering")
  public SimpleXsdSchema smartMeteringBundleXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_BUNDLE_XSD_PATH));
  }

  @Bean(name = "SmartMeteringMonitoring")
  public WsdlDefinition smartMeteringMonitoringWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_MONITORING_WSDL_PATH));
  }

  @Bean(name = "monitoring-ws-smartmetering")
  public SimpleXsdSchema smartMeteringMonitoringXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_MONITORING_XSD_PATH));
  }

  @Bean(name = "SmartMeteringAdhoc")
  public WsdlDefinition smartMeteringAdhocWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(SMART_METERING_ADHOC_WSDL_PATH));
  }

  @Bean(name = "adhoc-ws-smartmetering")
  public SimpleXsdSchema smartMeteringAdhocXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_ADHOC_XSD_PATH));
  }

  @Bean(name = "SmartMeteringConfiguration")
  public WsdlDefinition smartMeteringConfigurationWsdl() {
    return new SimpleWsdl11Definition(
        new ClassPathResource(SMART_METERING_CONFIGURATION_WSDL_PATH));
  }

  @Bean(name = "configuration-ws-smartmetering")
  public SimpleXsdSchema smartMeteringConfigurationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(SMART_METERING_CONFIGURATION_XSD_PATH));
  }
}
