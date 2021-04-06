/**
 * Copyright 2020 Alliander N.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.adapter.protocol.dlms.domain.commands.dlmsobjectconfig.model;

import org.opensmartgridplatform.adapter.protocol.dlms.exceptions.ProtocolAdapterException;

public enum CommunicationMethod {
    GPRS("GPRS"),
    CDMA("CDMA"),
    LTE_M("LTE_M");

    private final String methodName;

    CommunicationMethod(final String methodName) {
        this.methodName = methodName;
    }

    private String getMethodName() {
        return this.methodName;
    }

    public static CommunicationMethod getCommunicationMethod(final String method) throws ProtocolAdapterException {
        switch (method) {
            case "GPRS": return GPRS;
            case "CDMA": return CDMA;
            case "LTE_M": return LTE_M;
            default: throw new ProtocolAdapterException("Unknown communication method: " + method);
        }
    }
}
