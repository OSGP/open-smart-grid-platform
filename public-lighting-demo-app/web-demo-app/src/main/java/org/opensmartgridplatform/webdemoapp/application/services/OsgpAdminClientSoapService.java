/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.opensmartgridplatform.webdemoapp.application.services;

import org.opensmartgridplatform.webdemoapp.infra.platform.SoapRequestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.client.core.WebServiceTemplate;

import org.opensmartgridplatform.adapter.ws.schema.admin.devicemanagement.UpdateKeyRequest;

/**
 * Service class used for sending and receiving SOAP messages to/from the
 * platform.
 */
public class OsgpAdminClientSoapService {

    private static final String PUBLIC_KEY =
        "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==";
    private static final int OSLP_ELSTER_PROTOCOL = 4;

    @Autowired
    SoapRequestHelper soapRequestHelper;

    /**
     * Create a new UpdateKey Request adds it to the WebServiceTemplate and
     * sends it to the platform.
     */
    public void updateKeyRequest(final org.opensmartgridplatform.webdemoapp.domain.Device device) {

        final UpdateKeyRequest keyRequest = new UpdateKeyRequest();

        keyRequest.setDeviceIdentification(device.getDeviceIdentification());
        keyRequest.setPublicKey(PUBLIC_KEY);
        keyRequest.setProtocolInfoId(OSLP_ELSTER_PROTOCOL);

        final WebServiceTemplate template = this.soapRequestHelper.createAdminRequest();

        template.marshalSendAndReceive(keyRequest);
    }

}
