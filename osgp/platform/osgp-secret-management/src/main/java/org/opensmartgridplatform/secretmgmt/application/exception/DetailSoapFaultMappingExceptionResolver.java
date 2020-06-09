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
            detail.addFaultDetailElement(MESSAGE).addText(technicalFault.getMessage());
            detail.addFaultDetailElement(COMPONENT).addText(technicalFault.getComponent());
            detail.addFaultDetailElement(INNER_MESSAGE).addText(technicalFault.getInnerMessage());
            detail.addFaultDetailElement(INNER_EXCEPTION).addText(technicalFault.getInnerException());
        }
    }

}
