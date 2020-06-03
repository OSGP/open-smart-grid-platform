package org.opensmartgridplatform.secretmgmt.application.config;

import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.XsdSchemaCollection;
import org.springframework.xml.xsd.commons.CommonsXsdSchemaCollection;

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
}