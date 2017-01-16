package com.alliander.osgp.adapter.ws.shared.services;

import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;

@FunctionalInterface
public interface IWebserviceTemplateFactory {

    WebServiceTemplate getTemplate(final String organisationIdentification, final String userName,
            final String notificationURL) throws WebServiceSecurityException;
}
