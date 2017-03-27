/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.configuration;

import java.io.IOException;
import java.security.GeneralSecurityException;

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
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.configuration.SetClockConfigurationResponse;
import com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering.SmartMeteringBaseClient;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringConfigurationClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringConfigurationWstf;

    public GetAdministrativeStatusAsyncResponse getAdministrativeStatus(final GetAdministrativeStatusRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (GetAdministrativeStatusAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public GetAdministrativeStatusResponse retrieveGetAdministrativeStatusResponse(
            final GetAdministrativeStatusAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (GetAdministrativeStatusResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    public ReplaceKeysAsyncResponse replaceKeys(final ReplaceKeysRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        return (ReplaceKeysAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public ReplaceKeysResponse getReplaceKeysResponse(final ReplaceKeysAsyncRequest asyncRequest)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = asyncRequest.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (ReplaceKeysResponse) this.getTemplate().marshalSendAndReceive(asyncRequest);
    }

    public SetClockConfigurationAsyncResponse setClockConfiguration(final SetClockConfigurationRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (SetClockConfigurationAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public SetClockConfigurationResponse getSetClockConfigurationResponse(
            final SetClockConfigurationAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = request.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (SetClockConfigurationResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringConfigurationWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
    }
}
