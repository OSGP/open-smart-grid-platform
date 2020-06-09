package org.opensmartgridplatform.secretmgmt.application.exception;

import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TechnicalFault;

public class TechnicalServiceFaultException extends RuntimeException {
    private TechnicalFault technicalFault;

    public TechnicalServiceFaultException(String message, TechnicalFault technicalFault) {
        super(message);
        this.technicalFault = technicalFault;
    }

    public TechnicalServiceFaultException(String message, Throwable e, TechnicalFault technicalFault) {
        super(message, e);
        this.technicalFault = technicalFault;
    }

    public TechnicalFault getTechnicalFault() {
        return technicalFault;
    }

    public void setTechnicalFault(TechnicalFault serviceFault) {
        this.technicalFault = technicalFault;
        }
    }