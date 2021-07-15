/*
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.exception;

import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

@Slf4j
public class DetailSoapFaultMappingExceptionResolver extends SoapFaultMappingExceptionResolver {

  private static final QName MESSAGE = new QName("Message");
  private static final QName COMPONENT = new QName("Component");

  @Override
  protected void customizeFault(final Object endpoint, final Exception ex, final SoapFault fault) {
    log.error("Exception occured during SOAP request processing", ex);
    final SoapFaultDetail detail = fault.addFaultDetail();
    if (ex instanceof ExceptionWrapper) {
      this.customizeFault(endpoint, (Exception) ex.getCause(), fault);
      return;
    }

    if (ex.getMessage() != null) {
      String messageText = ex.getMessage();
      if (ex.getCause() != null) {
        messageText += ": " + ex.getCause().toString();
      }
      detail.addFaultDetailElement(MESSAGE).addText(messageText);
    }

    if (ex instanceof TechnicalException) {
      detail
          .addFaultDetailElement(COMPONENT)
          .addText(((TechnicalException) ex).getComponentType().name());
    }
  }
}
