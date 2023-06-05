// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.ws.admin.config;

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

  private static final String COMMON_XSD_PATH = "schemas/common-ws-admin.xsd";
  private static final String DEVICE_MANAGEMENT_XSD_PATH = "schemas/devicemanagement-ws-admin.xsd";

  private static final String DEVICE_MANAGEMENT_WSDL_PATH = "AdminDeviceManagement.wsdl";

  @Bean
  public PayloadValidatingInterceptor payloadValidatingInterceptor() {
    final PayloadValidatingInterceptor payloadValidatingInterceptor =
        new PayloadValidatingInterceptor();
    final Resource[] resources =
        new Resource[] {
          new ClassPathResource(COMMON_XSD_PATH), new ClassPathResource(DEVICE_MANAGEMENT_XSD_PATH),
        };
    payloadValidatingInterceptor.setSchemas(resources);
    return payloadValidatingInterceptor;
  }

  @Bean(name = "common-ws-admin")
  public SimpleXsdSchema commonXsd() {
    return new SimpleXsdSchema(new ClassPathResource(COMMON_XSD_PATH));
  }

  @Bean(name = "AdminDeviceManagement")
  public WsdlDefinition deviceManagementWsdl() {
    return new SimpleWsdl11Definition(new ClassPathResource(DEVICE_MANAGEMENT_WSDL_PATH));
  }

  @Bean(name = "devicemanagement-ws-admin")
  public SimpleXsdSchema deviceManagementXsd() {
    return new SimpleXsdSchema(new ClassPathResource(DEVICE_MANAGEMENT_XSD_PATH));
  }
}
