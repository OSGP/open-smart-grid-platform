/**
 * Copyright 2020 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.secretmanagement.application.exception;

import org.opensmartgridplatform.schemas.security.secretmanagement._2020._05.TechnicalFault;

public class TechnicalServiceFaultException extends RuntimeException {
    private TechnicalFault technicalFault;

    public TechnicalServiceFaultException(String message) {
        super(message);

        technicalFault = new TechnicalFault();
        technicalFault.setMessage(message);
    }

    public TechnicalServiceFaultException(String message, Throwable e, TechnicalFault technicalFault) {
        super(message, e);
        this.technicalFault = technicalFault;
    }

    public TechnicalFault getTechnicalFault() {
        return technicalFault;
    }

}