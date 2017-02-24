/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.cucumber.platform.dlms.support.ws.smartmetering;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;

import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataAsyncResponse;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataRequest;
import com.alliander.osgp.adapter.ws.schema.smartmetering.monitoring.ProfileGenericDataResponse;
import com.alliander.osgp.shared.exceptionhandling.WebServiceSecurityException;
import com.alliander.osgp.shared.infra.ws.DefaultWebServiceTemplateFactory;

@Component
public class SmartMeteringMonitoringManagementClient extends SmartMeteringBaseClient {

    @Autowired
    private DefaultWebServiceTemplateFactory smartMeteringMonitoringManagementWstf;

    public ProfileGenericDataAsyncResponse requestProfileGenericData(final ProfileGenericDataRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return (ProfileGenericDataAsyncResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    public ProfileGenericDataResponse getProfileGenericDataResponse(final ProfileGenericDataAsyncRequest request)
            throws WebServiceSecurityException, GeneralSecurityException, IOException {

        final String correlationUid = request.getCorrelationUid();
        this.waitForDlmsResponseData(correlationUid);

        return (ProfileGenericDataResponse) this.getTemplate().marshalSendAndReceive(request);
    }

    private WebServiceTemplate getTemplate() throws WebServiceSecurityException, GeneralSecurityException, IOException {
        return this.smartMeteringMonitoringManagementWstf.getTemplate(this.getOrganizationIdentification(),
                this.getUserName());
    }
}
