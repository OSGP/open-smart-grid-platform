/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.infra.ws;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.soap.security.support.KeyStoreFactoryBean;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

public class WebServiceTemplateFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceTemplateFactory.class);

    private Map<String, WebServiceTemplate> webServiceTemplates;
    private final Lock lock = new ReentrantLock();

    private static final String ORGANISATION_IDENTIFICATION_HEADER = "OrganisationIdentification";
    private static final String USER_NAME_HEADER = "UserName";
    private static final String APPLICATION_NAME_HEADER = "ApplicationName";

    private static final String NAMESPACE = "http://www.alliander.com/schemas/osp/common";

    private final Jaxb2Marshaller marshaller;
    private final SaajSoapMessageFactory messageFactory;
    private final String defaultUri;
    private final String keyStoreType;
    private final String keyStoreLocation;
    private final String keyStorePassword;
    private final KeyStoreFactoryBean trustStoreFactory;
    private final String applicationName;

    public WebServiceTemplateFactory(final Jaxb2Marshaller marshaller, final SaajSoapMessageFactory messageFactory,
            final String applicationName) {
        this(marshaller, messageFactory, null, null, null, null, null, applicationName);
    }

    public WebServiceTemplateFactory(final Jaxb2Marshaller marshaller, final SaajSoapMessageFactory messageFactory,
            final String keyStoreType, final String keyStoreLocation, final String keyStorePassword,
            final KeyStoreFactoryBean trustStoreFactory, final String applicationName) {
        this(marshaller, messageFactory, null, keyStoreType, keyStoreLocation, keyStorePassword, trustStoreFactory,
                applicationName);
    }

    public WebServiceTemplateFactory(final Jaxb2Marshaller marshaller, final SaajSoapMessageFactory messageFactory,
            final String defaultUri, final String keyStoreType, final String keyStoreLocation,
            final String keyStorePassword, final KeyStoreFactoryBean trustStoreFactory) {
        this(marshaller, messageFactory, defaultUri, keyStoreType, keyStoreLocation, keyStorePassword,
                trustStoreFactory, null);
    }

    public WebServiceTemplateFactory(final Jaxb2Marshaller marshaller, final SaajSoapMessageFactory messageFactory,
            final String defaultUri, final String keyStoreType, final String keyStoreLocation,
            final String keyStorePassword, final KeyStoreFactoryBean trustStoreFactory, final String applicationName) {
        this.marshaller = marshaller;
        this.messageFactory = messageFactory;
        this.defaultUri = defaultUri;
        this.keyStoreType = keyStoreType;
        this.keyStoreLocation = keyStoreLocation;
        this.keyStorePassword = keyStorePassword;
        this.trustStoreFactory = trustStoreFactory;
        this.applicationName = applicationName;
        this.webServiceTemplates = new HashMap<>();
    }

    public WebServiceTemplate getTemplate(final String organisationIdentification, final String userName)
            throws WebServiceSecurityException {
        return this.getTemplate(organisationIdentification, userName, this.applicationName);
    }

    public WebServiceTemplate getTemplate(final String organisationIdentification, final String userName,
            final String applicationName) throws WebServiceSecurityException {

        if (StringUtils.isEmpty(organisationIdentification)) {
            LOGGER.error("organisationIdentification is empty or null");
        }
        if (StringUtils.isEmpty(userName)) {
            LOGGER.error("userName is empty or null");
        }
        if (StringUtils.isEmpty(applicationName)) {
            LOGGER.error("applicationName is empty or null");
        }

        WebServiceTemplate webServiceTemplate = null;
        try {
            this.lock.lock();

            // Create new webservice template, if not yet available for
            // organisation
            final String key = organisationIdentification.concat("-").concat(userName).concat(applicationName);
            if (!this.webServiceTemplates.containsKey(key)) {
                this.webServiceTemplates.put(key,
                        this.createTemplate(organisationIdentification, userName, applicationName));
            }

            webServiceTemplate = this.webServiceTemplates.get(key);
        } finally {
            this.lock.unlock();
        }

        return webServiceTemplate;
    }

    private WebServiceTemplate createTemplate(final String organisationIdentification, final String userName,
            final String applicationName) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

        webServiceTemplate.setDefaultUri(this.defaultUri);
        webServiceTemplate.setMarshaller(this.marshaller);
        webServiceTemplate.setUnmarshaller(this.marshaller);
        webServiceTemplate.setInterceptors(new ClientInterceptor[] {
                new OrganisationIdentificationClientInterceptor(organisationIdentification, userName, applicationName,
                        NAMESPACE, ORGANISATION_IDENTIFICATION_HEADER, USER_NAME_HEADER, APPLICATION_NAME_HEADER) });
        if (this.defaultUri.contains("proxy-server")) {
            webServiceTemplate.setCheckConnectionForFault(false);
        } else {
            webServiceTemplate.setCheckConnectionForFault(true);
        }

        try {
            webServiceTemplate.setMessageSender(this.webServiceMessageSender(organisationIdentification));
        } catch (GeneralSecurityException | IOException e) {
            LOGGER.error("Webservice exception occurred: Certificate not available", e);
            throw new WebServiceSecurityException("Certificate not available", e);
        }

        return webServiceTemplate;
    }

    private HttpComponentsMessageSender webServiceMessageSender(final String keystore)
            throws GeneralSecurityException, IOException {

        // Open keystore, assuming same identity
        final KeyStoreFactoryBean keyStoreFactory = new KeyStoreFactoryBean();
        keyStoreFactory.setType(this.keyStoreType);
        keyStoreFactory.setLocation(new FileSystemResource(this.keyStoreLocation + "/" + keystore + ".pfx"));
        keyStoreFactory.setPassword(this.keyStorePassword);
        keyStoreFactory.afterPropertiesSet();

        final KeyStore keyStore = keyStoreFactory.getObject();
        if (keyStore == null || keyStore.size() == 0) {
            throw new KeyStoreException("Key store is empty");
        }

        // Setup SSL context, load trust and keystore and build the message
        // sender
        final SSLContext sslContext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, this.keyStorePassword.toCharArray())
                .loadTrustMaterial(this.trustStoreFactory.getObject(), new TrustSelfSignedStrategy()).build();

        final HttpClientBuilder clientbuilder = HttpClientBuilder.create();
        final SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext,
                SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        clientbuilder.setSSLSocketFactory(connectionFactory);

        // Add intercepter to prevent issue with duplicate headers.
        // See also:
        // http://forum.spring.io/forum/spring-projects/web-services/118857-spring-ws-2-1-4-0-httpclient-proxy-content-length-header-already-present
        clientbuilder.addInterceptorFirst(new HttpComponentsMessageSender.RemoveSoapHeadersInterceptor());

        return new HttpComponentsMessageSender(clientbuilder.build());
    }
}
