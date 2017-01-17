/**
 * Copyright 2017 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.cucumber.support.ws;

import javax.xml.namespace.QName;

import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.namespace.QNameUtils;

public class WebServiceClientInterceptor implements ClientInterceptor {
    private final String organisationIdentification;
    private final String userName;
    private final String applicationName;
    private final String namespace;
    private final String organisationIdentificationHeaderName;
    private final String userNameHeaderName;
    private final String applicationNameHeaderName;

    public WebServiceClientInterceptor(final String organisationIdentification, final String userName,
            final String applicationName, final String namespace, final String oraganisationIdentificationHeaderName,
            final String userNameHeaderName, final String applicationNameHeaderName) {
        this.organisationIdentification = organisationIdentification;
        this.userName = userName;
        this.applicationName = applicationName;
        this.namespace = namespace;
        this.organisationIdentificationHeaderName = oraganisationIdentificationHeaderName;
        this.userNameHeaderName = userNameHeaderName;
        this.applicationNameHeaderName = applicationNameHeaderName;
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext) {
        final SoapMessage soapMessage = (SoapMessage) messageContext.getRequest();
        final SoapHeader soapHeader = soapMessage.getSoapHeader();

        if (this.organisationIdentification != null) {
            final QName organisationIdentificationHeaderQName = QNameUtils.createQName(this.namespace,
                    this.organisationIdentificationHeaderName, "");
            final SoapHeaderElement organisationElement = soapHeader
                    .addHeaderElement(organisationIdentificationHeaderQName);
            organisationElement.setText(this.organisationIdentification);
        }

        final QName applicationNameHeaderQName = QNameUtils.createQName(this.namespace, this.applicationNameHeaderName,
                "");
        final SoapHeaderElement applicationElement = soapHeader.addHeaderElement(applicationNameHeaderQName);
        applicationElement.setText(this.applicationName);

        final QName userNameHeaderQName = QNameUtils.createQName(this.namespace, this.userNameHeaderName, "");
        final SoapHeaderElement userElement = soapHeader.addHeaderElement(userNameHeaderQName);
        userElement.setText(this.userName);

        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext messageContext) {
        return true;
    }

    @Override
    public boolean handleFault(final MessageContext messageContext) {
        return true;
    }

    @Override
    public void afterCompletion(final MessageContext messageContext, final Exception ex) {
        // Nothing needed here.
    }
}
