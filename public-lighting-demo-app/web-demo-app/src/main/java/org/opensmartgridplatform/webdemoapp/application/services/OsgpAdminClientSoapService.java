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

import ma.glasnost.orika.MapperFacade;

/**
 * Service class used for sending and receiving SOAP messages to/from the
 * platform.
 *
 */
public class OsgpAdminClientSoapService {

    @Autowired
    SoapRequestHelper soapRequestHelper;

    private final MapperFacade adminAdHocMapperFacade;

    public OsgpAdminClientSoapService(final MapperFacade mapper) {
        this.adminAdHocMapperFacade = mapper;
    }

    /**
     * Create a new UpdateKey Request adds it to the WebServiceTemplate and
     * sends it to the platform.
     */
    public void updateKeyRequest(final org.opensmartgridplatform.webdemoapp.domain.Device device) {

        final UpdateKeyRequest keyRequest = new UpdateKeyRequest();

        keyRequest.setDeviceIdentification(device.getDeviceIdentification());
        keyRequest.setPublicKey(
                "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEFhUImXFJdqmputquVAc2CPdnn9Ju00M3m/Ice7wABNN+oAYKQbw/OceqvZmFF1+r4nO/vCm/f1JO5nEorE2jNQ==");
        keyRequest.setProtocolInfoId(1);

        final WebServiceTemplate template = this.soapRequestHelper.createAdminRequest();

        template.marshalSendAndReceive(keyRequest);

    }

}
