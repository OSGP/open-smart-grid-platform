/*
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.ws.smartmetering.application.exceptionhandling;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.FunctionalFault;
import org.opensmartgridplatform.adapter.ws.schema.smartmetering.common.TechnicalFault;
import org.opensmartgridplatform.shared.exceptionhandling.FunctionalException;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

public class DetailSoapFaultMappingExceptionResolver extends SoapFaultMappingExceptionResolver {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(DetailSoapFaultMappingExceptionResolver.class);

  private SoapFaultMapper mapper = null;

  public DetailSoapFaultMappingExceptionResolver(final SoapFaultMapper soapFaultMapper) {
    this.mapper = soapFaultMapper;
  }

  @Override
  protected void customizeFault(final Object endpoint, final Exception ex, final SoapFault fault) {
    final SoapFaultDetail detail = fault.addFaultDetail();
    final Result result = detail.getResult();

    FunctionalException fex = null;
    TechnicalException tex = null;
    if (ex instanceof FunctionalException) {
      fex = (FunctionalException) ex;
    } else if (ex instanceof TechnicalException) {
      tex = (TechnicalException) ex;
    }

    if (fex != null) {
      try {
        this.marshalFunctionalException(fex, result);
      } catch (final JAXBException e) {
        LOGGER.error("Unable to marshal the Functional Exception", e);
      }
    }

    if (tex != null) {
      try {
        this.marshalTechnicalException(tex, result);
      } catch (final JAXBException e) {
        LOGGER.error("Unable to marshal the Technical Exception", e);
      }
    }
  }

  private void marshalFunctionalException(final FunctionalException fex, final Result result)
      throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(FunctionalFault.class);
    final Marshaller marshaller = context.createMarshaller();
    marshaller.marshal(this.mapper.map(fex, FunctionalFault.class), result);
  }

  private void marshalTechnicalException(final TechnicalException tex, final Result result)
      throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(TechnicalFault.class);
    final Marshaller marshaller = context.createMarshaller();
    marshaller.marshal(this.mapper.map(tex, TechnicalFault.class), result);
  }
}
