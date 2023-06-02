//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.ws.da.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.ws.soap.server.endpoint.interceptor.PayloadValidatingInterceptor;
import org.springframework.ws.wsdl.WsdlDefinition;
import org.springframework.ws.wsdl.wsdl11.SimpleWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;

@Configuration
public class DistributionAutomationWebServiceConfig {

  private static final String COMMON_XSD_PATH = "schemas/common-ws-distributionautomation.xsd";
  private static final String DISTRIBUTION_AUTOMATION_XSD_PATH =
      "schemas/distributionautomation.xsd";

  private static final String ADHOC_MANAGEMENT_WSDL_PATH =
      "DistributionAutomationAdHocManagement.wsdl";
  private static final String DEVICE_MANAGEMENT_WSDL_PATH =
      "DistributionAutomationDeviceManagement.wsdl";
  private static final String MONITORING_WSDL_PATH = "DistributionAutomationMonitoring.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH),
          new ClassPathResource(DISTRIBUTION_AUTOMATION_XSD_PATH)
        };
    payloadValidatingInterceptor.setSchemas(resources);
    return payloadValidatingInterceptor;
  }

  @Bean(name = "common-ws-distributionautomation")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "DistributionAutomationAdHocManagement")
  public WsdlDefinition adHocManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(ADHOC_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "DistributionAutomationMonitoring")
  public WsdlDefinition monitoringWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(MONITORING_WSDL_PATH));
  }

  @Bean(name = "DistributionAutomationDeviceManagement")
  public WsdlDefinition deviceManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "distributionautomation")
  public SimpleXsdSchema distributionAutomationXsd() {
    return new SimpleXsdSchema(new ClassPathResource(DISTRIBUTION_AUTOMATION_XSD_PATH));
  }
}
