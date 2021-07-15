/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.endpointinterceptors;

import java.util.ArrayList;
import java.util.Collection;
import org.apache.commons.lang3.StringUtils;
import org.opensmartgridplatform.domain.core.exceptions.CertificateInvalidException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.server.endpoint.interceptor.EndpointInterceptorAdapter;

/**
 * {@link EndpointInterceptorAdapter} which authorizes requests based on a certificate's RDN
 * attribute values and SOAP headers available in the {@link MessageContext}.
 */
public class CertificateAndSoapHeaderAuthorizationEndpointInterceptor
    extends EndpointInterceptorAdapter {

  private final String rdnAttributeValuesPropertyName;
  private final String soapHeaderPropertyName;

  /**
   * Creates an instance of {@link CertificateAndSoapHeaderAuthorizationEndpointInterceptor}.
   *
   * @param rdnAttributeValuesPropertyName the property name of the RDN attribute value.
   * @param soapHeaderPropertyName the property name of the SOAP header.
   */
  public CertificateAndSoapHeaderAuthorizationEndpointInterceptor(
      final String rdnAttributeValuesPropertyName, final String soapHeaderPropertyName) {
    this.rdnAttributeValuesPropertyName = rdnAttributeValuesPropertyName;
    this.soapHeaderPropertyName = soapHeaderPropertyName;
  }

  /** {@inheritDoc} */
  @Override
  public boolean handleRequest(final MessageContext messageContext, final Object endpoint)
      throws Exception {
    final Collection<String> rdnAttributeValues =
        this.getCollectionFromMessageContext(messageContext, this.rdnAttributeValuesPropertyName);
    final String soapHeaderValue =
        this.getStringFromMessageContext(messageContext, this.soapHeaderPropertyName);

    if (rdnAttributeValues.isEmpty() || StringUtils.isBlank(soapHeaderValue)) {
      this.logger.warn("Soapheader or CN is empty.");
      throw new CertificateInvalidException(soapHeaderValue);
    }

    if (rdnAttributeValues.contains(soapHeaderValue)) {
      return true;
    }

    this.logger.warn("Access is not granted because CN and Header are not equal");
    throw new CertificateInvalidException(soapHeaderValue);
  }

  /**
   * Gets a collection from a property of {@link MessageContext}.
   *
   * @param messageContext the {@link MessageContext} from which to get the collection.
   * @param propertyName the property name with which to get the collection.
   * @return the collection or an empty collection if none can be found.
   */
  @SuppressWarnings("unchecked")
  private Collection<String> getCollectionFromMessageContext(
      final MessageContext messageContext, final String propertyName) {
    final Object propertyValue = messageContext.getProperty(propertyName);

    if (propertyValue instanceof Collection) {
      return (Collection<String>) propertyValue;
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * Gets a string from a property of {@link MessageContext}.
   *
   * @param messageContext the {@link MessageContext} from which to get the string.
   * @param propertyName the property name with which to get the collection.
   * @return the string or an empty string if none can be found.
   */
  private String getStringFromMessageContext(
      final MessageContext messageContext, final String propertyName) {
    final Object propertyValue = messageContext.getProperty(propertyName);

    if (propertyValue instanceof String) {
      return (String) propertyValue;
    } else {
      return "";
    }
  }
}
