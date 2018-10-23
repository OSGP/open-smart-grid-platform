/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.infra.platform;

import javax.xml.namespace.QName;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.SoapMessage;

/**
 * Customized ClientInterceptor. Defines parameters for Soap request (such as
 * header, namespace, orgId, etc).
 *
 */
public class IdentificationClientInterceptor implements ClientInterceptor {

    private String orgId;
    private String userName;
    private String appName;
    private String namespace;
    private String orgIdHeaderName;
    private String userNameHeaderName;
    private String appNameHeaderName;

    public IdentificationClientInterceptor(final String orgId, final String userName, final String appName,
            final String namespace, final String orgIdHeaderName, final String userNameHeaderName,
            final String appNameHeaderName) {
        this.orgId = orgId;
        this.userName = userName;
        this.appName = appName;
        this.namespace = namespace;
        this.orgIdHeaderName = orgIdHeaderName;
        this.userNameHeaderName = userNameHeaderName;
        this.appNameHeaderName = appNameHeaderName;

    }

    @Override
    public void afterCompletion(final MessageContext arg0, final Exception arg1) throws WebServiceClientException {

    }

    @Override
    public boolean handleFault(final MessageContext arg0) throws WebServiceClientException {
        return true;
    }

    @Override
    public boolean handleRequest(final MessageContext msgContext) throws WebServiceClientException {
        final SoapMessage message = (SoapMessage) msgContext.getRequest();
        final SoapHeader header = message.getSoapHeader();

        final QName orgIdHeaderHeaderQName = new QName(this.namespace, this.orgIdHeaderName);
        final SoapHeaderElement orgElement = header.addHeaderElement(orgIdHeaderHeaderQName);
        orgElement.setText(this.orgId);

        final QName appNameHeaderQName = new QName(this.namespace, this.appNameHeaderName);
        final SoapHeaderElement appElement = header.addHeaderElement(appNameHeaderQName);
        appElement.setText(this.appName);

        final QName userNameHeaderQName = new QName(this.namespace, this.userNameHeaderName);
        final SoapHeaderElement userElement = header.addHeaderElement(userNameHeaderQName);
        userElement.setText(this.userName);

        return true;
    }

    @Override
    public boolean handleResponse(final MessageContext arg0) throws WebServiceClientException {
        return true;
    }

}
