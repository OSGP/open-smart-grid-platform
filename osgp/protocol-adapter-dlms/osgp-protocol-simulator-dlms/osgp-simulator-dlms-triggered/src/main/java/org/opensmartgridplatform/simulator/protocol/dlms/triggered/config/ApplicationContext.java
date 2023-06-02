//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.simulator.protocol.dlms.triggered.config;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.opensmartgridplatform.shared.config.MetricsConfig;
import org.opensmartgridplatform.shared.filters.NoCacheResponseFilter;
import org.opensmartgridplatform.simulator.protocol.dlms.dynamic.DlmsAttributeValuesResource;
import org.opensmartgridplatform.simulator.protocol.dlms.triggered.api.SimulatorTriggerResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ComponentScan(basePackages = {"org.opensmartgridplatform.simulator.protocol.dlms.triggered"})
@Import({MetricsConfig.class})
@ImportResource({"classpath:applicationContext.xml"})
public class ApplicationContext {

  @ApplicationPath("/")
  public static class JaxRsApiApplication extends Application {}

  @Bean(destroyMethod = "shutdown")
  public SpringBus cxf() {
    return new SpringBus();
  }

  @Bean
  @DependsOn("cxf")
  public Server jaxRsServer() {

    final JAXRSServerFactoryBean sf = new JAXRSServerFactoryBean();

    final List<Object> serviceBeans = new ArrayList<>();
    serviceBeans.add(this.simulatorTriggerResource());
    serviceBeans.add(this.dlmsAttributeValuesResource());
    sf.setServiceBeans(serviceBeans);

    final List<Object> providerBeans = new ArrayList<>(2);
    providerBeans.add(this.jacksonJsonProvider());
    providerBeans.add(this.noCacheFilter());
    sf.setProviders(providerBeans);

    sf.getInInterceptors().add(this.loggingInInterceptor());
    sf.getInFaultInterceptors().add(this.loggingInInterceptor());
    sf.getOutInterceptors().add(this.loggingOutInterceptor());
    sf.getOutFaultInterceptors().add(this.loggingOutInterceptor());

    sf.setAddress("/wakeup");

    return sf.create();
  }

  @Bean
  public SimulatorTriggerResource simulatorTriggerResource() {
    return new SimulatorTriggerResource();
  }

  @Bean
  public DlmsAttributeValuesResource dlmsAttributeValuesResource() {
    return new DlmsAttributeValuesResource();
  }

  @Bean
  public ContainerResponseFilter noCacheFilter() {
    return new NoCacheResponseFilter();
  }

  @Bean
  public JaxRsApiApplication jaxRsApiApplication() {
    return new JaxRsApiApplication();
  }

  @Bean
  public AbstractLoggingInterceptor loggingInInterceptor() {
    final AbstractLoggingInterceptor interceptor = new LoggingInInterceptor();
    interceptor.setPrettyLogging(true);
    return interceptor;
  }

  @Bean
  public AbstractLoggingInterceptor loggingOutInterceptor() {
    final AbstractLoggingInterceptor interceptor = new LoggingOutInterceptor();
    interceptor.setPrettyLogging(true);
    return interceptor;
  }

  @Bean
  public JacksonJsonProvider jacksonJsonProvider() {
    return new JacksonJsonProvider();
  }
}
