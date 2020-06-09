package org.opensmartgridplatform.secretmgmt.application.config;

import org.opensmartgridplatform.secretmgmt.application.exception.DetailSoapFaultMappingExceptionResolver;
import org.opensmartgridplatform.secretmgmt.application.exception.TechnicalServiceFaultException;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.soap.server.endpoint.SoapFaultDefinition;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

import java.util.Properties;

@EnableWs
@Configuration
public class WebServiceConfig extends WsConfigurerAdapter {
    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/SecretManagement/*");
    }

    /**
     * url of the WSDL by this definition is:
     *
     *    http://localhost:8080/ws/SecretManagement/secretManagement.wsdl
     */
    @Bean(name = "secretManagement")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchemaCollection secretManagementSchemas) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("SecretManagementPort");
        wsdl11Definition.setLocationUri("/ws/SecretManagement");
        wsdl11Definition.setTargetNamespace("http://www.opensmartgridplatform.org/schemas/security/secretmanagement/2020/05");
        wsdl11Definition.setSchemaCollection(secretManagementSchemas);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchemaCollection secretManagementSchemas() {
        CommonsXsdSchemaCollection sc = new CommonsXsdSchemaCollection();
        sc.setXsds(new ClassPathResource("schemas/secretmgmt.xsd"));
        return sc;
    }

    @Bean
    public SoapFaultMappingExceptionResolver exceptionResolver() {
        SoapFaultMappingExceptionResolver exceptionResolver = new DetailSoapFaultMappingExceptionResolver();

        SoapFaultDefinition faultDefinition = new SoapFaultDefinition();
        faultDefinition.setFaultCode(SoapFaultDefinition.SERVER);
        exceptionResolver.setDefaultFault(faultDefinition);

        Properties errorMappings = new Properties();
        errorMappings.setProperty(Exception.class.getName(), SoapFaultDefinition.SERVER.toString());
        errorMappings.setProperty(TechnicalServiceFaultException.class.getName(), SoapFaultDefinition.SERVER.toString());
        exceptionResolver.setExceptionMappings(errorMappings);
        exceptionResolver.setOrder(1);

        return exceptionResolver;
    }
}