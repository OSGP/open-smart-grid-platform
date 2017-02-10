/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.platform.dlms.cucumber.support.ws.smartmetering.installation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.installation.AddDeviceResponse;
import com.alliander.osgp.platform.dlms.cucumber.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringInstallationClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringInstallationManagementWstf;

    public AddDeviceAsyncResponse addDevice(final AddDeviceRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.smartMeteringInstallationManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (AddDeviceAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public AddDeviceResponse getAddDeviceResponse(final AddDeviceAsyncRequest asyncRequest)
            throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        final WebServiceTemplate webServiceTemplate = this.smartMeteringInstallationManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (AddDeviceResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }
}