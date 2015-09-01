/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.adapter.ws.smartmetering.infra.ws;

 import javax.xml.namespace.QName;

 import org.springframework.ws.client.WebServiceClientException;
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
     public boolean handleRequest(final MessageContext messageContext) throws WebServiceClientException {
         final SoapMessage soapMessage = (SoapMessage) messageContext.getRequest();
         final SoapHeader soapHeader = soapMessage.getSoapHeader();

         final QName organisationIdentificationHeaderName = QNameUtils.createQName(this.namespace,
                 this.organisationIdentificationHeaderName, "");
         final SoapHeaderElement organisationElement = soapHeader.addHeaderElement(organisationIdentificationHeaderName);
         organisationElement.setText(this.organisationIdentification);

         final QName applicationNameHeaderName = QNameUtils.createQName(this.namespace, this.applicationNameHeaderName,
                 "");
         final SoapHeaderElement applicationElement = soapHeader.addHeaderElement(applicationNameHeaderName);
         applicationElement.setText(this.applicationName);

         final QName userNameHeaderName = QNameUtils.createQName(this.namespace, this.userNameHeaderName, "");
         final SoapHeaderElement userElement = soapHeader.addHeaderElement(userNameHeaderName);
         userElement.setText(this.userName);

         return true;
     }

     @Override
     public boolean handleResponse(final MessageContext messageContext) throws WebServiceClientException {
         return true;
     }

     @Override
     public boolean handleFault(final MessageContext messageContext) throws WebServiceClientException {
         return true;
     }
 }
