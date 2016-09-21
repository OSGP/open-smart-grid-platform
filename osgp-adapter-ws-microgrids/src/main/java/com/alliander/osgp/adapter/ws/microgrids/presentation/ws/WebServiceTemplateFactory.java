/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.microgrids.presentation.ws;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;
import org.springframework.ws.transport.http.HttpComponentsMessageSender;

import com.alliander.osgp.adapter.ws.microgrids.exceptions.WebServiceSecurityException;

public class WebServiceTemplateFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServiceTemplateFactory.class);

    private final Map<String, WebServiceTemplate> webServiceTemplates;
    private final Lock lock = new ReentrantLock();

    private final String applicationName;

    private final Jaxb2Marshaller marshaller;
    private final SaajSoapMessageFactory messageFactory;

    public WebServiceTemplateFactory(final Jaxb2Marshaller marshaller, final SaajSoapMessageFactory messageFactory,
            final String applicationName) {
        this.marshaller = marshaller;
        this.messageFactory = messageFactory;
        this.applicationName = applicationName;
        this.webServiceTemplates = new HashMap<>();
    }

    public WebServiceTemplate getTemplate(final String organisationIdentification, final String userName,
            final String notificationURL) throws WebServiceSecurityException {

        if (StringUtils.isEmpty(organisationIdentification)) {
            LOGGER.error("organisationIdentification is empty or null");
        }
        if (StringUtils.isEmpty(userName)) {
            LOGGER.error("userName is empty or null");
        }
        if (StringUtils.isEmpty(this.applicationName)) {
            LOGGER.error("applicatioName is empty or null");
        }

        WebServiceTemplate webServiceTemplate = null;
        try {
            this.lock.lock();

            // Create new webservice template, if not yet available for
            // organisation
            final String key = organisationIdentification.concat("-").concat(userName).concat(this.applicationName);
            if (!this.webServiceTemplates.containsKey(key)) {
                this.webServiceTemplates.put(key, this.createTemplate(notificationURL));
            }

            webServiceTemplate = this.webServiceTemplates.get(key);

        } finally {
            this.lock.unlock();
        }

        return webServiceTemplate;
    }

    private WebServiceTemplate createTemplate(final String notificationUrl) {
        final WebServiceTemplate webServiceTemplate = new WebServiceTemplate(this.messageFactory);

        webServiceTemplate.setDefaultUri(notificationUrl);
        webServiceTemplate.setMarshaller(this.marshaller);
        webServiceTemplate.setUnmarshaller(this.marshaller);
        webServiceTemplate.setMessageSender(this.webServiceMessageSender());

        return webServiceTemplate;
    }

    private HttpComponentsMessageSender webServiceMessageSender() {
        return new HttpComponentsMessageSender();
    }
}
