/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.shared.infra.jms;

import java.io.Serializable;

public class RequestMessage implements Serializable {

    /**
     * Serial Version UID.
     */
    private static final long serialVersionUID = -9061131896296150992L;

    protected String correlationUid;
    protected String organisationIdentification;
    protected String deviceIdentification;
    protected Serializable request;

    public RequestMessage(final String correlationUid, final String organisationIdentification,
            final String deviceIdentification, final Serializable request) {
        this.correlationUid = correlationUid;
        this.organisationIdentification = organisationIdentification;
        this.deviceIdentification = deviceIdentification;
        this.request = request;
    }

    public String getCorrelationUid() {
        return this.correlationUid;
    }

    public String getOrganisationIdentification() {
        return this.organisationIdentification;
    }

    public String getDeviceIdentification() {
        return this.deviceIdentification;
    }

    public Serializable getRequest() {
        return this.request;
    }
}
