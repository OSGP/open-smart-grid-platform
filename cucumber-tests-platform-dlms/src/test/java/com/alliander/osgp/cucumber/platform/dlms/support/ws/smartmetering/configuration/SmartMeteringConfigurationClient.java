/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.GetAdministrativeStatusResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.ReplaceKeysResponse;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringConfigurationClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringConfigurationManagementWstf;

    public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(final GetAdministrativeStatusRequest request)
            throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.smartMeteringConfigurationManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetAdministrativeStatusAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            final GetAdministrativeStatusAsyncRequest asyncRequest) throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        final WebServiceTemplate webServiceTemplate = this.smartMeteringConfigurationManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (GetAdministrativeStatusResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }

    public ReplaceKeysAsyncResponse replaceKeys(final ReplaceKeysRequest request) throws WebServiceSecurityException {
        final WebServiceTemplate webServiceTemplate = this.smartMeteringConfigurationManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (ReplaceKeysAsyncResponse) webServiceTemplate.marshalSendAndReceive(request);
    }

    public ReplaceKeysResponse getReplaceKeysResponse(final ReplaceKeysAsyncRequest asyncRequest)
            throws WebServiceSecurityException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        final WebServiceTemplate webServiceTemplate = this.smartMeteringConfigurationManagementWstf
                .getTemplate(this.getOrganizationIdentification(), this.getUserName());
        return (ReplaceKeysResponse) webServiceTemplate.marshalSendAndReceive(asyncRequest);
    }
}
