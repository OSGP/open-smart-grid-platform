//  SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//  SPDX-License-Identifier: Apache-2.0
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import org.springframework.util.Assert;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.EndpointInterceptor;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;

/**
 * Intercept a SOAP Header and puts the given headerName and contextPropertyName in the
 * MessageContext.
 */
public class SoapHeaderInterceptor implements EndpointInterceptor {

  private final String headerName;
  private final String contextPropertyName;

  public SoapHeaderInterceptor(final String headerName, final String contextPropertyName) {
    this.headerName = headerName;
    this.contextPropertyName = contextPropertyName;
  }

  @Override
  public boolean handleRequest(final MessageContext messageContext, final Object endpoint)
      throws Exception {

    Assert.isInstanceOf(SoapMessage.class, messageContext.getRequest());
    final SoapMessage request = (SoapMessage) messageContext.getRequest();
    final SoapHeader soapHeader = request.getSoapHeader();

    // Try to get the value from the Soap Header.
    final String value =
        SoapHeaderEndpointInterceptorHelper.getHeaderValue(soapHeader, this.headerName);

    /*
     * Finally, set the property value into the message context, so it can
     * be used in the end point later.
     */
    messageContext.setProperty(this.contextPropertyName, value);

    // Return true so the interceptor chain will continue.
    return true;
  }

  @Override
  public boolean handleResponse(final MessageContext messageContext, final Object endpoint)
      throws Exception {
    return true;
  }

  @Override
  public boolean handleFault(final MessageContext messageContext, final Object endpoint)
      throws Exception {
    return true;
  }

  @Override
  public void afterCompletion(
      final MessageContext messageContext, final Object endpoint, final Exception ex)
      throws Exception {
    // Empty Method
  }
}
