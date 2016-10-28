/**
 * Copyright 2016 Smart Society Services B.V.
 */

package com.alliander.osgp.platform.dlms.cucumber.support;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;

import com.alliander.osgp.platform.cucumber.support.ApplicationConfig;

public class AbstractWebServiceConfig {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Value("${application.name}")
    protected String applicationName;

    @Value("${base.uri}")
    protected String baseUri;

    @Value("${web.service.truststore.location}")
    protected String webserviceTruststoreLocation;

    @Value("${web.service.truststore.password}")
    protected String webserviceTruststorePassword;

    @Value("${web.service.truststore.type}")
    protected String webserviceTruststoreType;

    @Value("${web.service.keystore.basepath}")
    protected String webserviceKeystoreLocation;

    @Value("${web.service.keystore.password}")
    protected String webserviceKeystorePassword;

    @Value("${web.service.keystore.type}")
    protected String webserviceKeystoreType;


    @Bean
    public SaajSoapMessageFactory messageFactory() {
        return new SaajSoapMessageFactory();
    }

    @Bean
    public KeyStoreFactoryBean webServiceTrustStoreFactory() {
        final KeyStoreFactoryBean factory = new KeyStoreFactoryBean();
        factory.setType(this.webserviceTruststoreType);
        factory.setLocation(new FileSystemResource(this.webserviceTruststoreLocation));
        factory.setPassword(this.webserviceTruststorePassword);

        return factory;
    }

}