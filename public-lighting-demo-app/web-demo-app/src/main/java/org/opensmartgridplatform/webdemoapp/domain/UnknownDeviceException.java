/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.domain;

/**
 * Custom exception to handle unknown devices.
 */
public class UnknownDeviceException extends Throwable {

    private static final long serialVersionUID = 1L;

    private final String soapFaultMessage;

    public UnknownDeviceException(final String faultStringOrReason) {
        this.soapFaultMessage = faultStringOrReason;
    }

    public String getSoapFaultMessage() {
        return this.soapFaultMessage;
    }

}
