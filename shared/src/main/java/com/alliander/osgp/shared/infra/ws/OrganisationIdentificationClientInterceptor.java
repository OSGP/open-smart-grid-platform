/**
 * Copyright 2014-2016 Smart Society Services B.V.
 */
package com.alliander.osgp.shared.infra.ws;

import javax.xml.namespace.QName;

import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.namespace.QNameUtils;

public class OrganisationIdentificationClientInterceptor implements ClientInterceptor {
    private final String organisationIdentification;
    private final String userName;
    private final String applicationName;
    private final String namespace;
    private final String organisationIdentificationHeaderName;
    private final String userNameHeaderName;
    private final String applicationNameHeaderName;

    public OrganisationIdentificationClientInterceptor(final String organisationIdentification, final String userName,
            final String applicationName, final String namespace, final String organisationIdentificationHeaderName,
            final String userNameHeaderName, final String applicationNameHeaderName) {
        this.organisationIdentification = organisationIdentification;
        this.userName = userName;
        this.applicationName = applicationName;
        this.organisationIdentificationHeaderName = organisationIdentificationHeaderName;
        this.userNameHeaderName = userNameHeaderName;
        this.applicationNameHeaderName = applicationNameHeaderName;

        this.namespace = namespace;
    }

    @Override
    public boolean handleRequest(final MessageContext messageContext) {
        final SoapMessage soapMessage = (SoapMessage) messageContext.getRequest();
        final SoapHeader soapHeader = soapMessage.getSoapHeader();

        final QName headerName = QNameUtils.createQName(this.namespace, this.organisationIdentificationHeaderName, "");
        final SoapHeaderElement element = soapHeader.addHeaderElement(headerName);
        element.setText(this.organisationIdentification);

        final QName qualifiedApplicationHeaderName = QNameUtils.createQName(this.namespace,
                this.applicationNameHeaderName, "");
        final SoapHeaderElement applicationElement = soapHeader.addHeaderElement(qualifiedApplicationHeaderName);
        applicationElement.setText(this.applicationName);

        final QName qualifiedUserHeaderName = QNameUtils.createQName(this.namespace, this.userNameHeaderName, "");
        final SoapHeaderElement userElement = soapHeader.addHeaderElement(qualifiedUserHeaderName);
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
        // Required to implement, but functionality not needed
    }
}
