package org.opensmartgridplatform.secretmgmt.application.exception;

import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TechnicalFault;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.SoapFaultDetail;
import org.springframework.ws.soap.server.endpoint.SoapFaultMappingExceptionResolver;

import javax.xml.namespace.QName;

public class DetailSoapFaultMappingExceptionResolver extends SoapFaultMappingExceptionResolver {

    private static final QName MESSAGE = new QName("Message");
    private static final QName COMPONENT = new QName("Component");
    private static final QName INNER_MESSAGE = new QName("InnerMessage");
    private static final QName INNER_EXCEPTION = new QName("InnerException");

    @Override
    protected void customizeFault(Object endpoint, Exception ex, SoapFault fault) {
        if (ex instanceof TechnicalServiceFaultException) {
            TechnicalFault technicalFault = ((TechnicalServiceFaultException) ex).getTechnicalFault();
            SoapFaultDetail detail = fault.addFaultDetail();
            if (technicalFault.getMessage() != null) detail.addFaultDetailElement(MESSAGE).addText(technicalFault.getMessage());
            if (technicalFault.getComponent() != null) detail.addFaultDetailElement(COMPONENT).addText(technicalFault.getComponent());
            if (technicalFault.getInnerMessage() != null) detail.addFaultDetailElement(INNER_MESSAGE).addText(technicalFault.getInnerMessage());
            if (technicalFault.getInnerException() != null) detail.addFaultDetailElement(INNER_EXCEPTION).addText(technicalFault.getInnerException());
        }
    }
}
