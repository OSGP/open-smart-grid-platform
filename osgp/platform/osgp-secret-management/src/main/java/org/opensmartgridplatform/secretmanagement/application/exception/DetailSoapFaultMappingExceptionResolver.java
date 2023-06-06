// SPDX-FileCopyrightText: Copyright Contributors to the GXF project
//
// SPDX-License-Identifier: Apache-2.0

package org.opensmartgridplatform.secretmanagement.application.exception;

import static org.opensmartgridplatform.secretmanagement.application.endpoints.SecretManagementEndpoint.CORRELATION_UID;
import static org.opensmartgridplatform.secretmanagement.application.endpoints.SecretManagementEndpoint.NAMESPACE_URI;

import java.util.Iterator;
import javax.xml.namespace.QName;
import lombok.extern.slf4j.Slf4j;
import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.SoapHeaderElement;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
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

  @Override
  protected void logException(final Exception ex, final MessageContext messageContext) {
    log.error(
        "[{}] Exception occurred during SOAP request processing",
        this.getCorrelationUid(messageContext),
        ex);
  }

  private String getCorrelationUid(final MessageContext messageContext) {
    final SaajSoapMessage request = (SaajSoapMessage) messageContext.getRequest();
    final Iterator<SoapHeaderElement> iter =
        request
            .getEnvelope()
            .getHeader()
            .examineHeaderElements(new QName(NAMESPACE_URI, CORRELATION_UID));
    return iter.hasNext() ? iter.next().getText() : null;
  }
}
