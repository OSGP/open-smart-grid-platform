/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.exceptions.EmptyApplicationNameSoapHeaderException;
import org.opensmartgridplatform.domain.core.exceptions.EmptyOrganisationIdentificationSoapHeaderException;
import org.opensmartgridplatform.domain.core.exceptions.EmptyUserNameSoapHeaderException;
import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

/** Intercept a SOAP Header and put the contents in the MessageContext. */
public class SoapHeaderEndpointInterceptor implements EndpointInterceptor {

  private final String organisationIdentificationHeaderName;
  private final String contextPropertyName;

  private static final String USER_NAME_HEADER_NAME = "UserName";
  private static final String APPLICTION_NAME_HEADER_NAME = "ApplicationName";

  public SoapHeaderEndpointInterceptor(final String headerName, final String contextPropertyName) {
    this.organisationIdentificationHeaderName = headerName;
    this.contextPropertyName = contextPropertyName;
  }

  @Override
  public boolean handleRequest(final MessageContext messageContext, final Object endpoint)
      throws Exception {

    Assert.isInstanceOf(SoapMessage.class, messageContext.getRequest());
    final SoapMessage request = (SoapMessage) messageContext.getRequest();
    final SoapHeader soapHeader = request.getSoapHeader();

    // Try to get the values from the Soap Header.
    final String organisationIdentification =
        SoapHeaderEndpointInterceptorHelper.getHeaderValue(
            soapHeader, this.organisationIdentificationHeaderName);
    final String userName =
        SoapHeaderEndpointInterceptorHelper.getHeaderValue(soapHeader, USER_NAME_HEADER_NAME);
    final String applicationName =
        SoapHeaderEndpointInterceptorHelper.getHeaderValue(soapHeader, APPLICTION_NAME_HEADER_NAME);

    // Check if the values are empty, if so, throw exception.
    if (StringUtils.isEmpty(organisationIdentification)) {
      throw new EmptyOrganisationIdentificationSoapHeaderException(organisationIdentification);
    }

    if (StringUtils.isEmpty(userName)) {
      throw new EmptyUserNameSoapHeaderException(userName);
    }

    if (StringUtils.isEmpty(applicationName)) {
      throw new EmptyApplicationNameSoapHeaderException(applicationName);
    }

    // Finally, set the organisation identification into the message
    // context, so it can be used in the end point later.
    messageContext.setProperty(this.contextPropertyName, organisationIdentification);

    // Return true so the interceptor chain will continue.
    return true;
  }

  @Override
  public boolean handleResponse(final MessageContext messageContext, final Object endpoint) {
    return true;
  }

  @Override
  public boolean handleFault(final MessageContext messageContext, final Object endpoint) {
    return true;
  }

  @Override
  public void afterCompletion(
      final MessageContext messageContext, final Object endpoint, final Exception ex) {
    // Empty Method
  }
}
