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
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

public class DetailSoapFaultMappingExceptionResolver extends SoapFaultMappingExceptionResolver {

    private static final QName MESSAGE = new QName("Message");
    private static final QName COMPONENT = new QName("Component");

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        SoapFaultDetail detail = fault.addFaultDetail();

        if (ex.getMessage() != null) {
            detail.addFaultDetailElement(MESSAGE).addText(ex.getMessage());
        }

        if (ex instanceof TechnicalException) {
            if (((TechnicalException) ex).getComponentType().name() != null) {
                detail.addFaultDetailElement(COMPONENT).addText(((TechnicalException) ex).getComponentType().name());
            }
        }
    }

}
