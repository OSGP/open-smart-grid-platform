// SPDX-FileCopyrightText: 2023 Contributors to the GXF project
// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.adapter.ws.da.application.exceptionhandling;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Result;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.FunctionalFault;
import org.opensmartgridplatform.adapter.ws.schema.distributionautomation.common.TechnicalFault;
import org.opensmartgridplatform.shared.exceptionhandling.ConnectionFailureException;
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
    ConnectionFailureException cex = null;
    if (ex instanceof FunctionalException) {
      fex = (FunctionalException) ex;
    } else if (ex instanceof TechnicalException) {
      tex = (TechnicalException) ex;
    } else if (ex instanceof ConnectionFailureException) {
      cex = (ConnectionFailureException) ex;
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

    if (cex != null) {
      try {
        this.marshalConnectionFailureException(cex, result);
      } catch (final JAXBException e) {
        LOGGER.error("Unable to marshal the Connection Failure Exception", e);
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

  private void marshalConnectionFailureException(
      final ConnectionFailureException cex, final Result result) throws JAXBException {
    final JAXBContext context = JAXBContext.newInstance(TechnicalFault.class);
    final Marshaller marshaller = context.createMarshaller();
    marshaller.marshal(this.mapper.map(cex, TechnicalFault.class), result);
  }
}
