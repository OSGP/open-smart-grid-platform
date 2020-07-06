/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.exception;

import javax.xml.namespace.QName;

import org.opensmartgridplatform.shared.exceptionhandling.TechnicalException;
import org.opensmartgridplatform.ws.schema.core.secret.management.TechnicalFault;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

public class DetailSoapFaultMappingExceptionResolver extends SoapFaultMappingExceptionResolver {

    private static final QName MESSAGE = new QName("Message");
    private static final QName COMPONENT = new QName("Component");
    private static final QName INNER_MESSAGE = new QName("InnerMessage");
    private static final QName INNER_EXCEPTION = new QName("InnerException");

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        if (ex instanceof TechnicalException) {
            TechnicalFault technicalFault = convert((TechnicalException) ex);
            SoapFaultDetail detail = fault.addFaultDetail();
            if (technicalFault.getMessage() != null) {
                detail.addFaultDetailElement(MESSAGE).addText(technicalFault.getMessage());
            }
            if (technicalFault.getComponent() != null) {
                detail.addFaultDetailElement(COMPONENT).addText(technicalFault.getComponent());
            }
            if (technicalFault.getInnerMessage() != null) {
                detail.addFaultDetailElement(INNER_MESSAGE).addText(technicalFault.getInnerMessage());
            }
            if (technicalFault.getInnerException() != null) {
                detail.addFaultDetailElement(INNER_EXCEPTION).addText(technicalFault.getInnerException());
            }
        }
    }

    private TechnicalFault convert(final TechnicalException ex) {
        if (ex == null) {
            return null;
        }
        final TechnicalFault destination = new TechnicalFault();
        destination.setComponent(ex.getComponentType().name());
        destination.setMessage(ex.getMessage());
        if (ex.getCause() != null) {
            destination.setInnerException(ex.getCause().getClass().getName());
            destination.setInnerMessage(ex.getCause().getMessage());
        }

        return destination;
    }
}
